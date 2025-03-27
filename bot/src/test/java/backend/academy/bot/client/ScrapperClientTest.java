package backend.academy.bot.client;

import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.ApiErrorResponse;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@WireMockTest(httpPort = 9090)
public class ScrapperClientTest {

    @SneakyThrows
    @Test
    @DisplayName("correct error handling from scrapper client")
    public void testHandlingErrorResponse() {
        WireMockStubUtil.mockListLinksNotFound();
        ScrapperClient scrapperClient = scrapperClient();

        ApiErrorResponse response = scrapperClient.listLinks(100L).apiErrorResponse();

        Assertions.assertThat(response).extracting(ApiErrorResponse::code).isEqualTo("404");
    }

    @SneakyThrows
    @Test
    @DisplayName("success get list links and returns correct response from Scrapper client")
    public void testListLinksReturnCorrectValue() {
        WireMockStubUtil.mockListLinksSuccess();
        ScrapperClient scrapperClient = scrapperClient();

        ListLinksResponse response = scrapperClient.listLinks(100L).answer();

        Assertions.assertThat(response)
            .extracting(ListLinksResponse::links)
            .isEqualTo(List.of(new LinkResponse(100L, URI.create("https://test.com"), null, null)));
    }

    @SneakyThrows
    @Test
    @DisplayName("success add link and returns correct response from Scrapper client")
    public void testAddLinkReturnCorrectValue() {
        WireMockStubUtil.mockAddLinkSuccess();
        ScrapperClient scrapperClient = scrapperClient();

        LinkResponse response = scrapperClient.addLink(100L,
            new AddLinkRequest(URI.create("https://test.com"), null, null)).answer();

        Assertions.assertThat(response)
            .extracting(LinkResponse::id, LinkResponse::url)
            .contains(100L, URI.create("https://test.com"));
    }

    @SneakyThrows
    @Test
    @DisplayName("success remove link and returns correct response from Scrapper client")
    public void testRemoveLinkReturnCorrectValue() {
        WireMockStubUtil.mockRemoveLinkSuccess();
        ScrapperClient scrapperClient = scrapperClient();

        LinkResponse response =
            scrapperClient.removeLink(100L, new RemoveLinkRequest(URI.create("https://test.com"))).answer();

        Assertions.assertThat(response)
            .extracting(LinkResponse::id, LinkResponse::url)
            .contains(100L, URI.create("https://test.com"));
    }

    private static ScrapperClient scrapperClient() {
        WebClient webClient = WebClient.builder()
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .baseUrl("http://localhost:9090")
            .build();

        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                WebClientAdapter.create(webClient))
            .build();

        return httpServiceProxyFactory.createClient(ScrapperClient.class);
    }
}
