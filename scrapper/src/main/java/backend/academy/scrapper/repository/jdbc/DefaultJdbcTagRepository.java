package backend.academy.scrapper.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefaultJdbcTagRepository implements JdbcTagRepository {

    private final JdbcClient client;

    @Override
    public void add(Long chatLinkId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        String upsertTagSql = """
        INSERT INTO tag (name)
        VALUES (:tagName)
        ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
        RETURNING id
        """;

        List<Long> tagIds = tags.stream()
            .distinct()
            .map(tagName -> client.sql(upsertTagSql)
                .param("tagName", tagName)
                .query(Long.class)
                .single())
            .toList();

        String insertChatLinkTagSql = """
        INSERT INTO chat_link_tag (chat_link_id, tag_id)
        VALUES (:chatLinkId, :tagId)
        ON CONFLICT (chat_link_id, tag_id) DO NOTHING
        """;

        tagIds.forEach(tagId ->
            client.sql(insertChatLinkTagSql)
                .param("chatLinkId", chatLinkId)
                .param("tagId", tagId)
                .update()
        );
    }

    @Override
    public List<String> findAllByChatLink(Long chatId, Long linkId) {
        String sql = """
        SELECT t.name
        FROM tag t
        JOIN chat_link_tag clt ON t.id = clt.tag_id
        JOIN chat_link cl ON clt.chat_link_id = cl.id
        WHERE cl.chat_id = :chatId AND cl.link_id = :linkId
        """;

        return client.sql(sql)
            .param("chatId", chatId)
            .param("linkId", linkId)
            .query(String.class)
            .list();
    }

    @Override
    public void remove(String tag) {
        client.sql("DELETE FROM tag WHERE name = :tag")
            .param("tag", tag)
            .update();
    }

    @Override
    public List<Long> findByName(String tag) {
        String sql = """
        SELECT clt.chat_link_id
        FROM chat_link_tag clt
        JOIN tag t ON clt.tag_id = t.id
        WHERE t.name = :tag
        """;

        return client.sql(sql)
            .param("tag", tag)
            .query(Long.class)
            .list();
    }
}
