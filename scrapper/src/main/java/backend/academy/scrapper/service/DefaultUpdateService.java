package backend.academy.scrapper.service;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.OptionalAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUpdateService implements UpdateService {

    private final BotClient botClient;

    @Override
    public OptionalAnswer<Void> sendUpdatesToUsers(LinkUpdate linkUpdate) {
        return botClient.handleUpdates(linkUpdate);
    }
}
