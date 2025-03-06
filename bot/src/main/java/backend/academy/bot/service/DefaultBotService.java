package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.OptionalAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultBotService implements BotService {

    private final ScrapperClient scrapperClient;

    @Override
    public OptionalAnswer<Void> registerUser(Long id) {
        return scrapperClient.registerChat(id);
    }

    @Override
    public OptionalAnswer<LinkResponse> linkUrlToUser(String url, Long userId, List<String> tags, List<String> filters) {
        return scrapperClient.addLink(userId, new AddLinkRequest(URI.create(url), tags, filters));
    }

    @Override
    public OptionalAnswer<LinkResponse> unlinkUrlFromUser(URI link, Long userId) {
        return scrapperClient.removeLink(userId, new RemoveLinkRequest(link));
    }

    @Override
    public OptionalAnswer<ListLinksResponse> getAllLinks(Long userId) {
        return scrapperClient.listLinks(userId);
    }
}
