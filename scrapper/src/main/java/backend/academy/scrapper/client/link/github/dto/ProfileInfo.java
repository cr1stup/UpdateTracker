package backend.academy.scrapper.client.link.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record ProfileInfo(
        @JsonProperty("full_name") String fullName,
        @JsonProperty("pushed_at") OffsetDateTime lastModified,
        String description) {
    public static final ProfileInfo EMPTY = new ProfileInfo(null, null, null);
}
