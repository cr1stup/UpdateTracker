package backend.academy.bot.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientProperties(@NotEmpty String scrapperUrl, Timeout timeout) {
    public record Timeout(
        Duration connect,
        Duration read,
        Duration write,
        Duration response
    ) {}
}
