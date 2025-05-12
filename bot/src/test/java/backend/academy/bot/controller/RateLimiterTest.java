package backend.academy.bot.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.LinkNotificationService;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(LinkUpdatesController.class)
@Import({RateLimiterTestConfig.class})
@TestPropertySource(properties = "ratelimiter.capacity=10")
public class RateLimiterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkNotificationService linkNotificationService;

    private final LinkUpdate update = new LinkUpdate(1L, null, "description", List.of(1L), false);

    @BeforeEach
    void setUp() {
        Mockito.reset(linkNotificationService);
    }

    @Test
    @SneakyThrows
    @DisplayName("POST /updates should return 429 after exceeding rate limit")
    public void handleUpdatesShouldReturn429AfterRateLimitExceeded() {
        Mockito.doNothing().when(linkNotificationService).notifyLinkUpdate(update);
        int capacity = 10;
        String requestBody =
                """
        {
            "id": 123,
            "url": "https://example.com",
            "description": "Some update",
            "tgChatIds": [1, 2, 3],
            "isBatch": false
        }
        """;

        for (int i = 0; i < capacity; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/updates")
                            .content(requestBody)
                            .contentType("application/json"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/updates")
                        .content(requestBody)
                        .contentType("application/json"))
                .andExpect(status().is(429));
    }
}
