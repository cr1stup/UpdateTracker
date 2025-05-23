package backend.academy.scrapper.client.link.github;

import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.client.link.github.dto.GithubResponse;
import backend.academy.scrapper.client.link.github.dto.ProfileInfo;
import backend.academy.scrapper.config.ClientProperties;
import backend.academy.scrapper.config.RetryConfig;
import backend.academy.scrapper.config.RetryProperties;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.EventInformation;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdateEvent;
import backend.academy.scrapper.util.LinkParser;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubClient implements LinkClient {
    private static final Pattern REPO_PATTERN = Pattern.compile("https://github.com/([^/]+)/([^/]+)");
    private final WebClient webClient;
    private final ClientProperties properties;

    @Autowired
    public GithubClient(
            WebClient.Builder webClientBuilder,
            ScrapperConfig config,
            ClientProperties properties,
            RetryProperties retryProperties) {
        this.webClient = webClientBuilder
                .baseUrl(properties.githubUrl())
                .defaultHeaders(headers -> {
                    if (config.githubToken() != null && !config.githubToken().isEmpty()) {
                        headers.set("Authorization", "Bearer " + config.githubToken());
                    }
                })
                .filter(RetryConfig.createFilter(retryProperties))
                .build();

        this.properties = properties;
    }

    @Override
    public boolean isSupport(URI url) {
        return LinkParser.isSupport(url, REPO_PATTERN);
    }

    @Override
    @CircuitBreaker(name = "apiClient")
    public LinkInformation fetchInformation(URI url) {
        Duration timeout = properties.timeout().global();

        var profileInfo =
                executeRequest(webClient, "/repos" + url.getPath(), ProfileInfo.class, ProfileInfo.EMPTY, timeout);

        var eventInfo = executeRequest(
                webClient,
                "/repos" + url.getPath() + "/issues",
                GithubResponse[].class,
                new GithubResponse[0],
                timeout);

        if (profileInfo == null || profileInfo.equals(ProfileInfo.EMPTY)) {
            return null;
        }

        List<LinkUpdateEvent> events = new ArrayList<>();
        EventInformation eventInformation =
                EventInformation.builder().message("обновление в репозитории").build();

        events.add(new LinkUpdateEvent(eventInformation, profileInfo.lastModified()));

        if (eventInfo != null && !Arrays.equals(eventInfo, new GithubResponse[0])) {
            GithubResponse lastEventInfo = eventInfo[0];
            eventInformation = EventInformation.builder()
                    .message("новый issue/PR:")
                    .title(lastEventInfo.title())
                    .user(lastEventInfo.user().login())
                    .createdAt(lastEventInfo.created_at())
                    .body(lastEventInfo.body())
                    .build();
            events.add(new LinkUpdateEvent(eventInformation, lastEventInfo.created_at()));
        }

        return new LinkInformation(url, profileInfo.fullName(), events);
    }
}
