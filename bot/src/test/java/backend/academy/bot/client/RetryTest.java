package backend.academy.bot.client;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import backend.academy.bot.service.BotService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@WireMockTest(httpPort = 9090)
@TestPropertySource(
        properties = {"client.scrapper-url=http://localhost:9090", "retry.max-attempts=2", "retry.retryable-codes=503"})
@SpringBootTest
public class RetryTest {

    @Autowired
    private BotService botService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void disableCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker("httpCircuitBreaker").transitionToDisabledState();
    }

    @SneakyThrows
    @Test
    @DisplayName("retry should work for error 503")
    public void testRetryForError503() {
        WireMockStubUtil.mockListLinksServerError();

        botService.getAllLinks(100L);

        WireMock.verify(3, getRequestedFor(urlEqualTo("/links")));
    }

    @SneakyThrows
    @Test
    @DisplayName("retry should not work for error 404")
    public void testRetryForError404() {
        WireMockStubUtil.mockListLinksNotFound();

        botService.getAllLinks(100L);

        WireMock.verify(1, getRequestedFor(urlEqualTo("/links")));
    }
}
