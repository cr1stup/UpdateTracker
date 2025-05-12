package backend.academy.bot.config;

import jakarta.validation.constraints.NotEmpty;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientProperties(@NotEmpty String scrapperUrl, Timeout timeout) {
    public record Timeout(Duration connect, Duration read, Duration write, Duration response) {}
}
