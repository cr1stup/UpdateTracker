package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.dto.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DefaultJdbcLinkRepository implements JdbcLinkRepository {

    private final JdbcClient client;

    @Override
    public Long add(Link link) {
        String sql = """
            INSERT INTO link (url, description, updated_at, last_checked_at)
            VALUES (:url, :description, :updatedAt, :lastCheckedAt)
            RETURNING id
            """;

        return client.sql(sql)
            .param("url", link.url())
            .param("description", link.description())
            .param("updatedAt", link.updatedAt())
            .param("lastCheckedAt", link.lastCheckedAt())
            .query(Long.class)
            .single();
    }

    @Override
    public List<Link> findLinksCheckedAfter(Duration after, int limit) {
        String sql = """
                SELECT * FROM link
                WHERE
                  last_checked_at < :last_checked_at
                ORDER BY last_checked_at
                LIMIT :limit
                """;

        return client.sql(sql)
            .param("last_checked_at", OffsetDateTime.now().minus(after))
            .param("limit", limit)
            .query(Link.class)
            .list();
    }

    @Override
    public Optional<Link> findById(long linkId) {
        return client.sql("SELECT * FROM link WHERE id = :id")
            .param("id", linkId)
            .query(Link.class)
            .optional();
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        return client.sql("SELECT * FROM link WHERE url = :url")
            .param("url", url)
            .query(Link.class)
            .optional();
    }

    @Override
    public void update(long id, OffsetDateTime lastModified) {
        client.sql("""
                UPDATE link
                SET last_checked_at = :last_checked_at,
                updated_at = :updated_at
                WHERE id = :id""")
            .param("last_checked_at", OffsetDateTime.now())
            .param("updated_at", lastModified)
            .param("id", id)
            .update();
    }

    @Override
    public void checkNow(long id) {
        client.sql("UPDATE link SET last_checked_at = :last_checked_at WHERE id = :id")
            .param("last_checked_at", OffsetDateTime.now())
            .param("id", id)
            .update();
    }

    @Override
    public void remove(long linkId) {
        client.sql("DELETE FROM link WHERE id = :id")
            .param("id", linkId)
            .update();
    }
}
