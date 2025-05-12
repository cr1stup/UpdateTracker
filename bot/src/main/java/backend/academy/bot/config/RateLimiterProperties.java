package backend.academy.bot.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ratelimiter", ignoreUnknownFields = false)
public record RateLimiterProperties(int capacity, int refillTokens, Duration refillDuration) {}
