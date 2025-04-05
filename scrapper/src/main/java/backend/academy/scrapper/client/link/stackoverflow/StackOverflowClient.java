package backend.academy.scrapper.client.link.stackoverflow;

import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.client.link.stackoverflow.dto.QuestionItem;
import backend.academy.scrapper.client.link.stackoverflow.dto.QuestionResponse;
import backend.academy.scrapper.client.link.stackoverflow.dto.StackOverflowResponse;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdateEvent;
import backend.academy.scrapper.util.LinkParser;
import java.net.URI;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StackOverflowClient implements LinkClient {
    private static final Pattern QUESTION_PATTERN = Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");
    private final WebClient webClient;

    @Autowired
    public StackOverflowClient(@Value("${api.stackoverflow.url}") String apiUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    @Override
    public boolean isSupport(URI url) {
        return LinkParser.isSupport(url, QUESTION_PATTERN);
    }

    @Override
    public LinkInformation fetchInformation(URI url) {
        String questionId = LinkParser.getQuestionId(url, QUESTION_PATTERN);
        String params = "?order=desc&sort=creation&site=stackoverflow&filter=withbody";

        var questionInfo = executeRequest(
                webClient,
                "/questions/" + questionId + "?site=stackoverflow",
                StackOverflowResponse.class,
                StackOverflowResponse.EMPTY);

        var answersInfo = executeRequest(
                webClient,
                "/questions/" + questionId + "/answers" + params,
                QuestionResponse.class,
                QuestionResponse.EMPTY);

        var commentsInfo = executeRequest(
                webClient,
                "/questions/" + questionId + "/comments" + params,
                QuestionResponse.class,
                QuestionResponse.EMPTY);

        if (questionInfo == null
                || questionInfo.items() == null
                || questionInfo.items().isEmpty()) {
            return null;
        }

        List<LinkUpdateEvent> events = new ArrayList<>();
        var questionItem = questionInfo.items().getFirst();
        events.add(new LinkUpdateEvent("обновление в вопросе", questionItem.lastModified()));

        if (answersInfo != null
                && answersInfo.items() != null
                && !answersInfo.items().isEmpty()) {
            String metaInformation = createMetaInformation(answersInfo.items().getFirst(), questionItem.title());
            events.add(new LinkUpdateEvent(
                    metaInformation, answersInfo.items().getFirst().creation_date()));
        }

        if (commentsInfo != null
                && commentsInfo.items() != null
                && !commentsInfo.items().isEmpty()) {
            String metaInformation = createMetaInformation(commentsInfo.items().getFirst(), questionItem.title());
            events.add(new LinkUpdateEvent(
                    metaInformation, commentsInfo.items().getFirst().creation_date()));
        }

        return new LinkInformation(url, questionInfo.items().getFirst().title(), events);
    }

    public String createMetaInformation(QuestionItem item, String title) {
        StringBuilder metaInformation = new StringBuilder();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
        String formattedTime = item.creation_date() != null ? formatter.format(item.creation_date()) : "N/A";

        metaInformation
                .append("новый комментарий/ответ:%n")
                .append("  • Title: ")
                .append(title != null ? title : "N/A")
                .append("%n")
                .append("  • User: ")
                .append(item.owner() != null ? item.owner().name() : "N/A")
                .append("%n")
                .append("  • Created at: ")
                .append(formattedTime)
                .append("%n")
                .append("  • Body: ")
                .append(
                        item.body() != null
                                ? item.body().length() > 200 ? item.body().substring(0, 200) + "..." : item.body()
                                : "No description")
                .append("%n");

        return metaInformation.toString().formatted();
    }
}
