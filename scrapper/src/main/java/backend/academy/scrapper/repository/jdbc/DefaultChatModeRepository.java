package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.dto.ChatMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultChatModeRepository implements JdbcChatModeRepository {

    private final JdbcClient client;

    @Override
    public Optional<ChatMode> findByChatId(Long chatId) {
        String sql =
                """
            SELECT cm.chat_id, m.name as name, cm.time
            FROM chat_mode cm
            JOIN mode m ON cm.mode_id = m.id
            WHERE cm.chat_id = :chatId
            """;

        return client.sql(sql).param("chatId", chatId).query(ChatMode.class).optional();
    }

    @Override
    public List<Long> findChatIdsByModeName(String modeName) {
        return client.sql(
                        """
                SELECT cm.chat_id
                FROM chat_mode cm
                JOIN mode m ON cm.mode_id = m.id
                WHERE m.name = :modeName
            """)
                .param("modeName", modeName)
                .query(Long.class)
                .list();
    }

    @Override
    public void insert(ChatMode chatMode) {
        client.sql(
                        """
                INSERT INTO chat_mode (chat_id, mode_id, time)
                VALUES (:chatId, (SELECT id FROM mode WHERE name = :modeName), :time)
            """)
                .param("chatId", chatMode.chatId())
                .param("modeName", chatMode.name())
                .param("time", chatMode.time())
                .update();
    }

    @Override
    public void update(ChatMode chatMode) {
        client.sql(
                        """
                UPDATE chat_mode
                SET mode_id = (SELECT id FROM mode WHERE name = :modeName), time = :time
                WHERE chat_id = :chatId
            """)
                .param("modeName", chatMode.name())
                .param("time", chatMode.time())
                .param("chatId", chatMode.chatId())
                .update();
    }

    @Override
    public void saveMode(String modeName) {
        client.sql("INSERT INTO mode (name) VALUES (:name)")
                .param("name", modeName)
                .update();
    }

    @Override
    public boolean existModeName(String modeName) {
        Integer count = client.sql("SELECT COUNT(*) FROM mode WHERE name = :name")
                .param("name", modeName)
                .query(Integer.class)
                .single();

        return count > 0;
    }
}
