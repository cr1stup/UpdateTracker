package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.dto.ChatMode;
import java.util.List;
import java.util.Optional;

public interface JdbcChatModeRepository {
    Optional<ChatMode> findByChatId(Long chatId);

    List<Long> findChatIdsByModeName(String modeName);

    void insert(ChatMode chatMode);

    void update(ChatMode chatMode);

    void saveMode(String modeName);

    boolean existModeName(String modeName);
}
