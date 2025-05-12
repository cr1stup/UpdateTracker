package backend.academy.scrapper.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.dto.ChatMode;
import backend.academy.scrapper.service.chat.ChatModeService;
import java.time.LocalTime;
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

@WebMvcTest(ChatModeController.class)
@Import({RateLimiterTestConfig.class})
@TestPropertySource(properties = "ratelimiter.capacity=10")
public class RateLimiterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatModeService chatModeService;

    @BeforeEach
    void setUp() {
        Mockito.reset(chatModeService);
    }

    @Test
    @SneakyThrows
    @DisplayName("GET /mode should return 429 after exceeding rate limit")
    public void getChatModeShouldReturn429AfterRateLimitExceeded() {
        Mockito.when(chatModeService.getMode(1L)).thenReturn(new ChatMode(1L, "immediate", LocalTime.of(3, 0)));
        int capacity = 10;

        for (int i = 0; i < capacity; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/mode")
                            .header("Tg-Chat-Id", 1L)
                            .contentType("application/json"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/mode")
                        .header("Tg-Chat-Id", 1L)
                        .contentType("application/json"))
                .andExpect(status().is(429));
    }
}
