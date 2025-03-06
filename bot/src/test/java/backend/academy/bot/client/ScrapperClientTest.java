package backend.academy.bot.client;

import backend.academy.bot.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@WireMockTest(httpPort = 9090)
public class ScrapperClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Test
    @DisplayName("correct error handling from scrapper client")
    public void testHandlingErrorResponse () {
        stubFor(
            get(urlPathMatching("/links"))
                .withHeader("Tg-Chat-Id", equalTo("100"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(objectMapper.writeValueAsString(
                        new ApiErrorResponse("Not found", "404", "Not found", "Not found", List.of())
                    ))
                    .withHeader("Content-Type", "application/json")
                )
        );
        ScrapperClient scrapperClient = scrapperClient();

        ApiErrorResponse response = scrapperClient.listLinks(100L).apiErrorResponse();

        Assertions.assertThat(response)
            .extracting(ApiErrorResponse::code)
            .isEqualTo("404");
    }

    private static ScrapperClient scrapperClient() {
        WebClient webClient = WebClient.builder()
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .baseUrl("http://localhost:9090").build();

        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(webClient))
            .build();

        return httpServiceProxyFactory.createClient(ScrapperClient.class);
    }
}
