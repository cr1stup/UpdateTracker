package backend.academy.bot.controller;

import backend.academy.bot.service.LinkNotificationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class RateLimiterTestConfig {

    @Bean
    public LinkNotificationService linkNotificationService() {
        return mock(LinkNotificationService.class);
    }

}
