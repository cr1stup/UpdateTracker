package backend.academy.scrapper.client.link;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientFactory {
    private final List<LinkClient> clients;

    public LinkClient getClient(URI url) {
        for (LinkClient client : clients) {
            if (client.isSupport(url)) {
                return client;
            }
        }

        return null;
    }
}
