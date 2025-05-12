package backend.academy.bot.controller;

import static org.mockito.Mockito.mock;

import backend.academy.bot.service.LinkNotificationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RateLimiterTestConfig {

    @Bean
    public LinkNotificationService linkNotificationService() {
        return mock(LinkNotificationService.class);
    }
}
