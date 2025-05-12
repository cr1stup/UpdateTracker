package backend.academy.scrapper.client;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.update.HttpUpdateService;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@WireMockTest(httpPort = 9090)
@TestPropertySource(properties = {
    "client.bot-url=http://localhost:9090",
})
public class BotClientCircuitBreakerTest {

    @Autowired
    private HttpUpdateService updateService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private final LinkUpdate update = new LinkUpdate(1L, null, "description", List.of(1L), false);

    @BeforeEach
    void setup() {
        circuitBreakerRegistry.circuitBreaker("apiClient").reset();
    }

    @Test
    @DisplayName("botClient circuit Breaker should transition to OPEN state after service failures")
    void circuitBreakerShouldOpenAfterFailures() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("apiClient");
        WireMockStubUtil.mockBotError503();

        try {
            updateService.sendUpdatesToUsers(update);
        } catch (Exception e) {
            assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        }
    }
}
