package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "retry", ignoreUnknownFields = false)
public record RetryProperties(
    int maxAttempts,
    Duration waitDuration,
    List<Integer> retryableCodes) {}
