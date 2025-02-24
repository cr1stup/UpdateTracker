package backend.academy.bot.service;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.OptionalAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultBotService implements BotService {

    @Override
    public OptionalAnswer<Void> registerUser(Long id) {
        return null;
    }

    @Override
    public OptionalAnswer<LinkResponse> linkUrlToUser(String url, Long userId) {
        return null;
    }

    @Override
    public OptionalAnswer<LinkResponse>  unlinkUrlFromUser(Long linkId, Long userId) {
        return null;
    }

    @Override
    public OptionalAnswer<ListLinksResponse> showAllLinks(Long userId) {
        return null;
    }
}
