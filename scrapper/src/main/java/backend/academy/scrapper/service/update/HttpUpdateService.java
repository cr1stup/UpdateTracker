package backend.academy.scrapper.service.update;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HttpUpdateService implements ImmediateUpdateService {

    private final BotClient botClient;

    @Override
    public void sendUpdatesToUsers(LinkUpdate linkUpdate) {
        botClient.handleUpdates(linkUpdate);
    }

    @Override
    public String transportName() {
        return "http";
    }
}
