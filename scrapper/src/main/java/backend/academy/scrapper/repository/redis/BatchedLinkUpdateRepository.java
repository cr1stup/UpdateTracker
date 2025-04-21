package backend.academy.scrapper.repository.redis;

import backend.academy.scrapper.dto.LinkUpdate;
import java.util.List;

public interface BatchedLinkUpdateRepository {

    void addToBatch(Long chatId, LinkUpdate update);

    List<LinkUpdate> getBatchByChatId(Long chatId);

    void removeFromBatch(Long chatId);
}
