package backend.academy.scrapper.client.link;

import backend.academy.scrapper.dto.LinkInformation;
import java.net.URI;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClient;

public interface LinkClient {

    boolean isSupport(URI url);

    LinkInformation fetchInformation(URI url);

    default <T> T executeRequest(WebClient webClient, String uri, Class<T> type, T defaultValue, Duration timeout) {
        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(type)
                .timeout(timeout)
                .block();
    }
}
