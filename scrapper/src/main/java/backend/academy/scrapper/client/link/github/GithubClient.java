package backend.academy.scrapper.client.link.github;

import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.LinkInformation;
import java.net.URI;
import java.util.regex.Pattern;
import backend.academy.scrapper.util.LinkParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubClient implements LinkClient {
    private static final Pattern REPO_PATTERN = Pattern.compile("https://github.com/([^/]+)/([^/]+)");
    private final WebClient webClient;

    @Autowired
    public GithubClient(@Value("${api.github.url}") String apiUrl, WebClient.Builder webClientBuilder, ScrapperConfig config) {
        this.webClient = webClientBuilder
            .baseUrl(apiUrl)
            .defaultHeaders(headers -> {
                if (config.githubToken() != null && !config.githubToken().isEmpty()) {
                    headers.set("Authorization", "Bearer " + config.githubToken());
                }
            })
            .build();
    }

    @Override
    public boolean isSupport(URI url) {
        return LinkParser.isSupport(url, REPO_PATTERN);
    }

    @Override
    public LinkInformation fetchInformation(URI url) {
        var info = executeRequest(
            webClient,
            "/repos" + url.getPath(),
            RepositoryInfo.class,
            RepositoryInfo.EMPTY
        );

        if (info == null || info.equals(RepositoryInfo.EMPTY)) {
            return null;
        }

        return new LinkInformation(url, info.fullName(), info.description(), info.lastModified());
    }
}
