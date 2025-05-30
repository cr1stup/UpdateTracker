package backend.academy.bot.service;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.ChatMode;
import backend.academy.bot.dto.OptionalAnswer;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;

public interface BotService {

    OptionalAnswer<Void> registerUser(Long id);

    OptionalAnswer<LinkResponse> linkUrlToUser(String url, Long userId, List<String> tags, List<String> filters);

    OptionalAnswer<LinkResponse> unlinkUrlFromUser(URI link, Long userId);

    OptionalAnswer<ListLinksResponse> getAllLinks(Long userId);

    OptionalAnswer<Void> setChatMode(Long id, String mode, LocalTime time);

    OptionalAnswer<ChatMode> getChatMode(Long id);
}
