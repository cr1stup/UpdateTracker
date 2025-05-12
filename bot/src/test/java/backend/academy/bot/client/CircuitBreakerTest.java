package backend.academy.bot.client;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.bot.service.BotService;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@WireMockTest(httpPort = 9090)
@TestPropertySource(
        properties = {
            "client.scrapper-url=http://localhost:9090",
        })
public class CircuitBreakerTest {

    @Autowired
    private BotService botService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setup() {
        circuitBreakerRegistry.circuitBreaker("botService").reset();
    }

    @Test
    @DisplayName("circuit Breaker should transition to OPEN state after service failures")
    void circuitBreakerShouldOpenAfterFailures() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("botService");
        WireMockStubUtil.mockListLinksServerError();

        botService.getAllLinks(100L);

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
