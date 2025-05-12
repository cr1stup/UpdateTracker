package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.chat.ChatModeService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class RateLimiterTestConfig {

    @Bean
    public ChatModeService chatModeService() {
        return mock(ChatModeService.class);
    }

}
