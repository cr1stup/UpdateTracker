package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientProperties(@NotEmpty String botUrl, String githubUrl, String stackoverflowUrl, Timeout timeout) {

    public record Timeout(Duration connect, Duration read, Duration write, Duration response, Duration global) {}
}
