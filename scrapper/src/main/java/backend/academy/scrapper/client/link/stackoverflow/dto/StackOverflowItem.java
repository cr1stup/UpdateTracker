package backend.academy.scrapper.client.link.stackoverflow.dto;

import backend.academy.scrapper.client.link.stackoverflow.util.UnixTimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

public record StackOverflowItem(
    String title,
    @JsonProperty("last_edit_date")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    OffsetDateTime lastModified
) {
}
