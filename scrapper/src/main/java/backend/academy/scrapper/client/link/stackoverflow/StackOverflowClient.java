package backend.academy.scrapper.client.link.stackoverflow;

import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.client.link.stackoverflow.dto.QuestionItem;
import backend.academy.scrapper.client.link.stackoverflow.dto.QuestionResponse;
import backend.academy.scrapper.client.link.stackoverflow.dto.StackOverflowItem;
import backend.academy.scrapper.client.link.stackoverflow.dto.StackOverflowResponse;
import backend.academy.scrapper.config.ClientProperties;
import backend.academy.scrapper.config.RetryConfig;
import backend.academy.scrapper.config.RetryProperties;
import backend.academy.scrapper.dto.EventInformation;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdateEvent;
import backend.academy.scrapper.util.LinkParser;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StackOverflowClient implements LinkClient {
    private static final Pattern QUESTION_PATTERN = Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");
    private final WebClient webClient;
    private final ClientProperties properties;

    @Autowired
    public StackOverflowClient(
            WebClient.Builder webClientBuilder, ClientProperties properties, RetryProperties retryProperties) {
        this.webClient = webClientBuilder
                .baseUrl(properties.stackoverflowUrl())
                .filter(RetryConfig.createFilter(retryProperties))
                .build();
        this.properties = properties;
    }

    @Override
    public boolean isSupport(URI url) {
        return LinkParser.isSupport(url, QUESTION_PATTERN);
    }

    @Override
    @CircuitBreaker(name = "apiClient")
    public LinkInformation fetchInformation(URI url) {
        Duration timeout = properties.timeout().global();
        String questionId = LinkParser.getQuestionId(url, QUESTION_PATTERN);
        String params = "?order=desc&sort=creation&site=stackoverflow&filter=withbody";

        var questionInfo = executeRequest(
                webClient,
                "/questions/" + questionId + "?site=stackoverflow",
                StackOverflowResponse.class,
                StackOverflowResponse.EMPTY,
                timeout);

        var answersInfo = executeRequest(
                webClient,
                "/questions/" + questionId + "/answers" + params,
                QuestionResponse.class,
                QuestionResponse.EMPTY,
                timeout);

        var commentsInfo = executeRequest(
                webClient,
                "/questions/" + questionId + "/comments" + params,
                QuestionResponse.class,
                QuestionResponse.EMPTY,
                timeout);

        if (questionInfo == null
                || questionInfo.items() == null
                || questionInfo.items().isEmpty()) {
            return null;
        }

        List<LinkUpdateEvent> events = new ArrayList<>();
        var questionItem = questionInfo.items().getFirst();
        EventInformation eventInformation =
                EventInformation.builder().message("обновление в вопрсосе").build();

        events.add(new LinkUpdateEvent(eventInformation, questionItem.lastModified()));
        addEvent(answersInfo, events, questionItem);
        addEvent(commentsInfo, events, questionItem);

        return new LinkInformation(url, questionInfo.items().getFirst().title(), events);
    }

    private void addEvent(QuestionResponse info, List<LinkUpdateEvent> events, StackOverflowItem questionItem) {
        EventInformation eventInformation;
        if (info != null && info.items() != null && !info.items().isEmpty()) {
            QuestionItem item = info.items().getFirst();

            eventInformation = EventInformation.builder()
                    .message("новый комментарий/ответ:")
                    .title(questionItem.title())
                    .user(item.owner().name())
                    .createdAt(item.creation_date())
                    .body(item.body())
                    .build();

            events.add(new LinkUpdateEvent(
                    eventInformation, info.items().getFirst().creation_date()));
        }
    }
}
