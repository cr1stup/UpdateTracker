package backend.academy.scrapper.repository.jdbc;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultJdbcFilterRepository implements JdbcFilterRepository {

    private final JdbcClient client;

    @Override
    public void add(Long chatLinkId, List<String> filters) {
        if (filters == null || filters.isEmpty()) {
            return;
        }

        String upsertFilterSql =
                """
        INSERT INTO filter (name)
        VALUES (:filterName)
        ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
        RETURNING id
        """;

        List<Long> filterIds = filters.stream()
                .distinct()
                .map(filterName -> client.sql(upsertFilterSql)
                        .param("filterName", filterName)
                        .query(Long.class)
                        .single())
                .toList();

        String insertChatLinkFilterSql =
                """
        INSERT INTO chat_link_filter (chat_link_id, filter_id)
        VALUES (:chatLinkId, :filterId)
        ON CONFLICT (chat_link_id, filter_id) DO NOTHING
        """;

        filterIds.forEach(filterId -> client.sql(insertChatLinkFilterSql)
                .param("chatLinkId", chatLinkId)
                .param("filterId", filterId)
                .update());
    }

    @Override
    public List<String> findAllByChatLink(Long chatId, Long linkId) {
        String sql =
                """
        SELECT f.name
        FROM filter f
        JOIN chat_link_filter clf ON f.id = clf.filter_id
        JOIN chat_link cl ON clf.chat_link_id = cl.id
        WHERE cl.chat_id = :chatId AND cl.link_id = :linkId
        """;

        return client.sql(sql)
                .param("chatId", chatId)
                .param("linkId", linkId)
                .query(String.class)
                .list();
    }

    @Override
    public List<Long> findByName(String filter) {
        String sql =
                """
        SELECT clf.chat_link_id
        FROM chat_link_filter clf
        JOIN filter f ON clf.filter_id = f.id
        WHERE f.name = :filter
        """;

        return client.sql(sql).param("filter", filter).query(Long.class).list();
    }

    @Override
    public void remove(String filter) {
        client.sql("DELETE FROM filter WHERE name = :filter")
                .param("filter", filter)
                .update();
    }
}
