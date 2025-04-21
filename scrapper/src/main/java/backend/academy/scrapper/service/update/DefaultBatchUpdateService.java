package backend.academy.scrapper.service.update;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.repository.redis.DefaultBatchedLinkUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultBatchUpdateService implements BatchUpdateService {

    private final BotClient botClient;
    private final DefaultBatchedLinkUpdateRepository batchedRepository;

    @Override
    public void sendBatchUpdateToUser(Long chatId) {
        List<LinkUpdate> updates = batchedRepository.getBatchByChatId(chatId);
        StringBuilder summary = new StringBuilder();

        if (!updates.isEmpty()) {
            summary.append("У вас есть обновления:%n");
            for (LinkUpdate update : updates) {
                summary.append("- ").append(update.url()).append(":%n").append(update.description()).append("%n");
            }
        } else {
            summary.append("Обновлений не было");
        }

        LinkUpdate batched = new LinkUpdate(
            chatId,
            null,
            summary.toString().formatted(),
            List.of(chatId),
            true
        );
        botClient.handleUpdates(batched);
    }
}
