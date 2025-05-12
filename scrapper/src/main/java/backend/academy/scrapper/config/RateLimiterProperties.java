package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "ratelimiter", ignoreUnknownFields = false)
public record RateLimiterProperties (int capacity, int refillTokens, Duration refillDuration) {}
