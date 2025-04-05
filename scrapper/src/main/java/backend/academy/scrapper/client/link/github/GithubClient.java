package backend.academy.scrapper.client.link.github;

import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.client.link.github.dto.GithubResponse;
import backend.academy.scrapper.client.link.github.dto.ProfileInfo;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdateEvent;
import backend.academy.scrapper.util.LinkParser;
import java.net.URI;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubClient implements LinkClient {
    private static final Pattern REPO_PATTERN = Pattern.compile("https://github.com/([^/]+)/([^/]+)");
    private final WebClient webClient;

    @Autowired
    public GithubClient(
            @Value("${api.github.url}") String apiUrl, WebClient.Builder webClientBuilder, ScrapperConfig config) {
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
        var profileInfo = executeRequest(webClient, "/repos" + url.getPath(), ProfileInfo.class, ProfileInfo.EMPTY);
        var eventInfo = executeRequest(webClient, "/repos" + url.getPath() + "/issues", GithubResponse[].class, new GithubResponse[0]);

        if (profileInfo == null || profileInfo.equals(ProfileInfo.EMPTY)) {
            return null;
        }

        List<LinkUpdateEvent> events = new ArrayList<>();
        events.add(new LinkUpdateEvent("обновление в репозитории", profileInfo.lastModified()));

        if (eventInfo != null && !Arrays.equals(eventInfo, new GithubResponse[0])) {
            GithubResponse lastEventInfo = eventInfo[0];
            String metaInformation = createMetaInformation(lastEventInfo);
            events.add(new LinkUpdateEvent(metaInformation, lastEventInfo.created_at()));
        }

        return new LinkInformation(url, profileInfo.fullName(), events);
    }

    public String createMetaInformation(GithubResponse response) {
        StringBuilder metaInformation = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
        String formattedTime = response.created_at() != null ? formatter.format(response.created_at()) : "N/A";

        metaInformation.append("новый issue/PR:%n")
            .append("  • User: ").append(response.user() != null ? response.user().login() : "N/A").append("%n")
            .append("  • Title: ").append(response.title() != null ? response.title() : "N/A").append("%n")
            .append("  • Created at: ").append(formattedTime).append("%n")
            .append("  • Body: ").append(response.body() == null || response.body().isEmpty() ? "No description" :
                response.body().length() > 200 ?
                    response.body().substring(0, 200) + "..." :
                    response.body())
            .append("%n");

        return metaInformation.toString().formatted();
    }
}
