package backend.academy.scrapper.repository.jdbc;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultJdbcChatRepository implements JdbcChatRepository {

    private final JdbcClient client;

    @Override
    public void add(long chatId) {
        client.sql("INSERT INTO chat (id) VALUES (:chatId) ON CONFLICT (id) DO NOTHING")
                .param("chatId", chatId)
                .update();
    }

    @Override
    public void remove(long chatId) {
        client.sql("DELETE FROM chat WHERE id = :chatId")
                .param("chatId", chatId)
                .update();
    }

    @Override
    public boolean isExists(long chatId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM chat WHERE id = :chatId)";

        return Boolean.TRUE.equals(client.sql(sql)
                .param("chatId", chatId)
                .query(Boolean.class)
                .optional()
                .orElse(false));
    }

    @Override
    public List<Long> findAll() {
        return client.sql("SELECT (id) FROM chat").query(Long.class).list();
    }
}
