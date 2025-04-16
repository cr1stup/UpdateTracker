package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(
        @NotNull Scheduler scheduler,
        @NotEmpty String githubToken,
        StackOverflowCredentials stackOverflow,
        AccessType databaseAccessType,
        KafkaConfiguration kafka,
        MessageTransport messageTransport) {
    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}

    public record Scheduler(
            boolean enable,
            @NotNull Duration interval,
            @NotNull Duration forceCheckDelay,
            int batchSize,
            int threadCount) {}

    public enum AccessType {
        JDBC,
        JPA
    }

    public enum MessageTransport {
        HTTP,
        KAFKA
    }

    public record KafkaConfiguration(
        String updatesTopicName
    ) {}
}
