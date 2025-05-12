package backend.academy.scrapper.service.rateLimiter;

import backend.academy.scrapper.config.RateLimiterProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(RateLimiterProperties.class)
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService implements Filter {

    private final RateLimiterProperties properties;
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ip = getClientIp(httpRequest);

        Bucket bucket = ipBuckets.computeIfAbsent(ip, this::newBucket);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests from IP: " + ip);
            log.warn("Too many requests from IP: {}", ip);
        }
    }

    private Bucket newBucket(String ip) {
        Refill refill = Refill.greedy(properties.refillTokens(), properties.refillDuration());
        Bandwidth limit = Bandwidth.classic(properties.capacity(), refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }

        List<String> ips = Arrays.stream(xfHeader.trim().split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .toList();

        return ips.getFirst();
    }
}
