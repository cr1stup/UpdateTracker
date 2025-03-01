package backend.academy.bot.service;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.OptionalAnswer;
import java.net.URI;

public interface BotService {

    OptionalAnswer<Void> registerUser(Long id);

    OptionalAnswer<LinkResponse> linkUrlToUser(String url, Long userId);

    OptionalAnswer<LinkResponse> unlinkUrlFromUser(URI link, Long userId);

    OptionalAnswer<ListLinksResponse> getAllLinks(Long userId);
}
