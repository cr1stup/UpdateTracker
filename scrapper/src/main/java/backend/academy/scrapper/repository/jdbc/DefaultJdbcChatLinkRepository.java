package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.dto.Link;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultJdbcChatLinkRepository implements JdbcChatLinkRepository {

    private final JdbcClient client;

    @Override
    public List<Link> findAllByChatId(long chatId) {
        String sql =
                """
                SELECT
                  link.*
                FROM
                  chat_link
                  INNER JOIN link ON link.id = chat_link.link_id
                WHERE
                  chat_link.chat_id = ?""";

        return client.sql(sql).params(chatId).query(Link.class).list();
    }

    @Override
    public List<Long> findAllByLinkId(long linkId) {
        String sql = """
        SELECT chat_id
        FROM chat_link
        WHERE link_id = :linkId
        """;

        return client.sql(sql).param("linkId", linkId).query(Long.class).list();
    }

    @Override
    public Long add(long chatId, long linkId) {
        String sql =
                """
        INSERT INTO chat_link (chat_id, link_id)
        VALUES (:chatId, :linkId)
        RETURNING id
        """;

        return client.sql(sql)
                .param("chatId", chatId)
                .param("linkId", linkId)
                .query(Long.class)
                .single();
    }

    @Override
    public boolean isLinkAlreadyExist(long chatId, URI url) {
        String sql =
                """
        SELECT COUNT(*) > 0
        FROM chat_link cl
        JOIN link l ON cl.link_id = l.id
        WHERE cl.chat_id = :chatId AND l.url = :url
        """;

        return client.sql(sql)
                .param("chatId", chatId)
                .param("url", url.toString())
                .query(Boolean.class)
                .single();
    }

    @Override
    public void remove(long chatId, long linkId) {
        client.sql("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?")
                .params(List.of(chatId, linkId))
                .update();
    }
}
