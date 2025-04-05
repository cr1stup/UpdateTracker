package backend.academy.scrapper.client;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.client.link.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.link.stackoverflow.dto.QuestionItem;
import backend.academy.scrapper.client.link.stackoverflow.dto.QuestionResponse;
import backend.academy.scrapper.client.link.stackoverflow.dto.StackOverflowItem;
import backend.academy.scrapper.client.link.stackoverflow.dto.StackOverflowResponse;
import backend.academy.scrapper.dto.LinkInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class StackOverflowClientTest {

    private StackOverflowClient stackOverflowClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private final URI testUri = URI.create("https://stackoverflow.com/questions/12345/test-question");
    private final OffsetDateTime testTime = OffsetDateTime.of(2023, 12, 15, 14, 30, 0, 0, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        lenient().when(webClientBuilder.build()).thenReturn(webClient);

        stackOverflowClient = new StackOverflowClient("https://api.stackoverflow.test", webClientBuilder);

        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("information should format metadata with all fields")
    void testCreateMetaInformationFormatting() {
        var questionItem = new QuestionItem(
                new QuestionItem.Owner("testUser"),
                testTime,
                "This is a detailed answer body that explains the solution to the problem.");
        String questionTitle = "How to test Spring WebClient?";

        String result = stackOverflowClient.createMetaInformation(questionItem, questionTitle);

        String expected = String.format("новый комментарий/ответ:%n" + "  • Title: How to test Spring WebClient?%n"
                + "  • User: testUser%n"
                + "  • Created at: 15.12.2023 14:30%n"
                + "  • Body: This is a detailed answer body that explains the solution to the problem.%n");
        assertThat(result.trim()).isEqualTo(expected.trim());
    }

    @Test
    @DisplayName("information should include question update event when question changes")
    void testFetchInformationQuestionEvent() {
        var soItem = new StackOverflowItem("Question Title", testTime);
        mockQuestionResponse(soItem);
        mockEmptyAnswerResponse();

        LinkInformation result = stackOverflowClient.fetchInformation(testUri);

        assertThat(result).isNotNull();
        assertThat(result.url()).isEqualTo(testUri);
        assertThat(result.title()).isEqualTo("Question Title");
        assertThat(result.events()).hasSize(1);
        assertThat(result.events().getFirst().information()).isEqualTo("обновление в вопросе");
    }

    @Test
    @DisplayName("information should create both question and answer events")
    void testFetchInformationReturnAllEvents() {
        var soItem = new StackOverflowItem("Question Title", testTime);
        var questionItem = new QuestionItem(new QuestionItem.Owner("answerOwner"), testTime.plusDays(1), "Answer body");
        mockQuestionResponse(soItem);
        mockAnswerResponse(questionItem);

        LinkInformation result = stackOverflowClient.fetchInformation(testUri);

        assertThat(result).isNotNull();
        assertThat(result.events()).hasSize(2);
        assertThat(result.events().get(0).information()).contains("обновление в вопросе");
        assertThat(result.events().get(1).information()).contains("новый комментарий/ответ");
    }

    private void mockQuestionResponse(StackOverflowItem item) {
        when(responseSpec.bodyToMono(StackOverflowResponse.class))
                .thenReturn(Mono.just(new StackOverflowResponse(List.of(item))));
    }

    private void mockAnswerResponse(QuestionItem item) {
        when(responseSpec.bodyToMono(QuestionResponse.class))
                .thenReturn(Mono.just(new QuestionResponse(List.of(item))))
                .thenReturn(Mono.just(QuestionResponse.EMPTY));
    }

    private void mockEmptyAnswerResponse() {
        when(responseSpec.bodyToMono(QuestionResponse.class)).thenReturn(Mono.just(QuestionResponse.EMPTY));
    }
}
