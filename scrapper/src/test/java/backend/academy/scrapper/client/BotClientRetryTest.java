package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.update.HttpUpdateService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@WireMockTest(httpPort = 9090)
@TestPropertySource(
        properties = {"client.bot-url=http://localhost:9090", "retry.max-attempts=2", "retry.retryable-codes=503"})
@SpringBootTest
public class BotClientRetryTest {

    @Autowired
    private HttpUpdateService updateService;

    private final LinkUpdate update = new LinkUpdate(1L, null, "description", List.of(1L), false);

    @SneakyThrows
    @Test
    @DisplayName("botClient retry should work for error 503")
    public void testRetryForError503() {
        WireMockStubUtil.mockBotError503();

        try {
            updateService.sendUpdatesToUsers(update);
        } catch (Exception e) {
            WireMock.verify(3, postRequestedFor(urlEqualTo("/updates")));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("botClient retry should not work for error 404")
    public void testRetryForError404() {
        WireMockStubUtil.mockBotError404();

        updateService.sendUpdatesToUsers(update);

        WireMock.verify(1, postRequestedFor(urlEqualTo("/updates")));
    }
}
