package backend.academy.scrapper.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.client.link.github.GithubClient;
import backend.academy.scrapper.client.link.github.dto.GithubResponse;
import backend.academy.scrapper.config.ScrapperConfig;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class GithubInformationTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private GithubClient githubClient;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeaders(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        githubClient = new GithubClient(
                "https://api.github.com", webClientBuilder, new ScrapperConfig(null, null, null, null));
    }

    @Test
    @DisplayName("information should format complete data correctly")
    void testCreateMetaInformationFullData() {
        OffsetDateTime createdAt = OffsetDateTime.of(2023, 5, 15, 14, 30, 0, 0, ZoneOffset.UTC);
        GithubResponse response = new GithubResponse(
                "Test Issue",
                new GithubResponse.User("testUser"),
                createdAt,
                "This is a test issue body that is longer than 200 characters. ".repeat(5));

        String result = githubClient.createMetaInformation(response);

        String expected = String.format(
                "новый issue/PR:%n" + "  • User: testUser%n"
                        + "  • Title: Test Issue%n"
                        + "  • Created at: %s%n"
                        + "  • Body: This is a test issue body that is longer than 200 characters. This is a test issue body that is longer than 200 characters. This is a test issue body that is longer than 200 characters. This is a test...%n",
                createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneOffset.systemDefault())));
        assertEquals(expected.trim(), result.trim());
    }

    @Test
    @DisplayName("information should format null data correctly")
    void testCreateMetaInformationWithNullValues() {
        GithubResponse response = new GithubResponse(null, null, null, null);

        String result = githubClient.createMetaInformation(response);

        String expected = String.format("новый issue/PR:%n" + "  • User: N/A%n"
                + "  • Title: N/A%n"
                + "  • Created at: N/A%n"
                + "  • Body: No description%n");
        assertEquals(expected.trim(), result.trim());
    }
}
