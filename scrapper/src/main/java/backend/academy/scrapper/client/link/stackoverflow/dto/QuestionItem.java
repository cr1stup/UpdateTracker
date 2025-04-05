package backend.academy.scrapper.client.link.stackoverflow.dto;

import backend.academy.scrapper.client.link.stackoverflow.util.UnixTimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

public record QuestionItem(
        Owner owner,
        @JsonDeserialize(using = UnixTimestampDeserializer.class) OffsetDateTime creation_date,
        String body) {
    public record Owner(@JsonProperty("display_name") String name) {}
}
