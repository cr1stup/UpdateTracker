package backend.academy.scrapper.client.link.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

record RepositoryInfo(
        @JsonProperty("full_name") String fullName,
        @JsonProperty("pushed_at") OffsetDateTime lastModified,
        String description) {
    public static final RepositoryInfo EMPTY = new RepositoryInfo(null, null, null);
}
