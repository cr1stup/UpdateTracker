package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.dto.Link;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface JdbcLinkRepository {
    Long add(Link link);

    List<Link> findLinksCheckedAfter(Duration after, int limit);

    Optional<Link> findById(long linkId);

    Optional<Link> findByUrl(String url);

    void update(long id, OffsetDateTime lastModified);

    void checkNow(long id);

    void remove(long linkId);
}
