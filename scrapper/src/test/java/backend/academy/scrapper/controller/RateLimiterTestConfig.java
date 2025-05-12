package backend.academy.scrapper.controller;

import static org.mockito.Mockito.mock;

import backend.academy.scrapper.service.chat.ChatModeService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RateLimiterTestConfig {

    @Bean
    public ChatModeService chatModeService() {
        return mock(ChatModeService.class);
    }
}
