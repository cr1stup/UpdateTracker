package backend.academy.bot.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinkUpdateDeserializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Valid JSON is mapped correctly to the DTO")
    void validJsonShouldDeserializeToLinkUpdate() throws Exception {
        String json =
                """
                {
                    "id": 11,
                    "url": "https://example.com",
                    "description": "A test update",
                    "tgChatIds": [1001, 1002]
                }
            """;

        LinkUpdate result = objectMapper.readValue(json, LinkUpdate.class);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(11L);
        assertThat(result.url()).isEqualTo(URI.create("https://example.com"));
        assertThat(result.description()).isEqualTo("A test update");
        assertThat(result.tgChatIds()).containsExactly(1001L, 1002L);
    }
}
