package backend.academy.scrapper.database.jdbc;

import backend.academy.scrapper.database.JdbcTestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@SpringBootTest
public class JdbcFilterRepositoryTest extends JdbcTestUtil {

    @Test
    @Transactional
    @Rollback
    @DisplayName("add should insert filters and create relations with chatLink")
    void testAddFilters() {
        long chatId = 123L;
        Long linkId = addLink();
        addChat(chatId);
        List<String> filters = List.of("filter", "work", "jdbc");
        Long chatLinkId = addChatLink(chatId, linkId);

        filterRepository.add(chatLinkId, filters);

        List<String> savedTags = filterRepository.findAllByChatLink(chatId, linkId);
        Assertions.assertThat(savedTags).containsExactlyInAnyOrderElementsOf(filters);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("remove should delete filter from database")
    void testRemoveFilter() {
        long chatId = 123L;
        Long linkId = addLink();
        addChat(chatId);
        List<String> filters = List.of("filter", "work", "jdbc");
        Long chatLinkId = addChatLink(chatId, linkId);
        filterRepository.add(chatLinkId, filters);

        filterRepository.remove("filter");

        List<String> savedTags = filterRepository.findAllByChatLink(chatId, linkId);
        Assertions.assertThat(savedTags).doesNotContain("filter");
    }
}
