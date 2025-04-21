package backend.academy.scrapper.service.update;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.message-transport", havingValue = "http")
public class HttpUpdateService implements ImmediateUpdateService {

    private final BotClient botClient;

    @Override
    public void sendUpdatesToUsers(LinkUpdate linkUpdate) {
        botClient.handleUpdates(linkUpdate);
    }
}
