package backend.academy.bot.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;

public class WireMockStubUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static void mockListLinksNotFound() {
        stubFor(get(urlPathMatching("/links"))
                .withHeader("Tg-Chat-Id", equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(
                                new ApiErrorResponse("Not found", "404", "Not found", "Not found", List.of())))));
    }

    @SneakyThrows
    public static void mockListLinksServerError() {
        stubFor(get(urlPathMatching("/links"))
                .withHeader("Tg-Chat-Id", equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(new ApiErrorResponse(
                                "Scrapper service is unavailable or unresponsive",
                                "503",
                                "serverError",
                                "Сервис обработки запросов недоступен, повторите попытку позже",
                                List.of())))));
    }

    @SneakyThrows
    public static void mockListLinksSuccess() {
        stubFor(get(urlPathMatching("/links"))
                .withHeader("Tg-Chat-Id", equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(new ListLinksResponse(
                                List.of(new LinkResponse(100L, URI.create("https://test.com"), null, null)), 1)))));
    }

    @SneakyThrows
    public static void mockAddLinkSuccess() {
        stubFor(post(urlPathMatching("/links"))
                .withHeader("Tg-Chat-Id", equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(
                                new LinkResponse(100L, URI.create("https://test.com"), null, null)))));
    }

    @SneakyThrows
    public static void mockRemoveLinkSuccess() {
        stubFor(delete(urlPathMatching("/links"))
                .withHeader("Tg-Chat-Id", equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(
                                new LinkResponse(100L, URI.create("https://test.com"), null, null)))));
    }
}
