package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import backend.academy.scrapper.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;

public class WireMockStubUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static void mockBotError503() {
        stubFor(post(urlPathMatching("/updates"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withBody(objectMapper.writeValueAsString(
                                new ApiErrorResponse("Not found", "503", "Not found", "Not found", List.of())))
                        .withHeader("Content-Type", "application/json")));
    }

    @SneakyThrows
    public static void mockBotError404() {
        stubFor(post(urlPathMatching("/updates"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody(objectMapper.writeValueAsString(
                                new ApiErrorResponse("Not found", "404", "Not found", "Not found", List.of())))
                        .withHeader("Content-Type", "application/json")));
    }
}
