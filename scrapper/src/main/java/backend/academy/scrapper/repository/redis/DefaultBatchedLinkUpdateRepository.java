package backend.academy.scrapper.repository.redis;

import backend.academy.scrapper.dto.LinkUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultBatchedLinkUpdateRepository implements BatchedLinkUpdateRepository {

    private final RedisTemplate<String, LinkUpdate> redisTemplate;

    @Override
    public void addToBatch(Long chatId, LinkUpdate update) {
        String key = "batched-updates:" + chatId;
        redisTemplate.opsForList().rightPush(key, update);
    }

    @Override
    public List<LinkUpdate> getBatchByChatId(Long chatId) {
        String key = "batched-updates:" + chatId;
        List<LinkUpdate> batch = redisTemplate.opsForList().range(key, 0, -1);
        removeFromBatch(chatId);
        return batch != null ? batch : List.of();
    }

    @Override
    public void removeFromBatch(Long chatId) {
        String key = "batched-updates:" + chatId;
        redisTemplate.delete(key);
    }
}
