package backend.academy.bot.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "retry", ignoreUnknownFields = false)
public record RetryProperties(
    int maxAttempts,
    Duration waitDuration,
    List<Integer> retryableCodes) {}
