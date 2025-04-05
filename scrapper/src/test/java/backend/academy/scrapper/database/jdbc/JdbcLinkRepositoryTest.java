package backend.academy.scrapper.database.jdbc;

import backend.academy.scrapper.database.JdbcTestUtil;
import backend.academy.scrapper.dto.Link;
import java.time.OffsetDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JdbcLinkRepositoryTest extends JdbcTestUtil {

    @Test
    @Transactional
    @Rollback
    @DisplayName("add insert link in database")
    void testAddLinkInDatabase() {
        var link = Link.create("test.com", "test", OffsetDateTime.MIN, OffsetDateTime.MAX);

        var id = linkRepository.add(link);

        Assertions.assertThat(linkRepository.findById(id).get())
                .extracting(Link::url, Link::description)
                .contains(link.url(), link.description());
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("remove delete link from database")
    void testDeleteLinkFromDatabase() {
        var link = Link.create("test.com", "test", OffsetDateTime.MIN, OffsetDateTime.MAX);
        linkRepository.add(link);
        var dbLink = linkRepository.findByUrl(link.url());

        linkRepository.remove(dbLink.get().id());

        Assertions.assertThat(linkRepository.findByUrl(link.url()).isEmpty()).isTrue();
    }
}
