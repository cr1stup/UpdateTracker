package backend.academy.scrapper.client.link.stackoverflow;

import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.dto.LinkInformation;
import java.net.URI;
import java.util.regex.Pattern;
import backend.academy.scrapper.util.LinkParser;
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

        var info = executeRequest(
            webClient,
            "/questions/" + questionId + "?site=stackoverflow",
            StackOverflowResponse.class,
            StackOverflowResponse.EMPTY
        );

        if (info == null || info.items() == null || info.items().length == 0) {
            return null;
        }

        return new LinkInformation(url, info.items()[0].title(), null, info.items()[0].lastModified());
    }
}
