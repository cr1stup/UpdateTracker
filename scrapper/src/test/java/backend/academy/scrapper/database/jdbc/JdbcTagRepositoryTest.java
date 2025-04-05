package backend.academy.scrapper.database.jdbc;

import backend.academy.scrapper.database.JdbcTestUtil;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JdbcTagRepositoryTest extends JdbcTestUtil {

    @Test
    @Transactional
    @Rollback
    @DisplayName("add should insert tags and create relations with chatLink")
    void testAddTags() {
        long chatId = 123L;
        Long linkId = addLink();
        addChat(chatId);
        List<String> tags = List.of("java", "spring", "jdbc");
        Long chatLinkId = addChatLink(chatId, linkId);

        tagRepository.add(chatLinkId, tags);

        List<String> savedTags = tagRepository.findAllByChatLink(chatId, linkId);
        Assertions.assertThat(savedTags).containsExactlyInAnyOrderElementsOf(tags);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("remove should delete tag from database")
    void testRemoveTag() {
        long chatId = 123L;
        Long linkId = addLink();
        addChat(chatId);
        List<String> tags = List.of("java", "spring", "jdbc");
        Long chatLinkId = addChatLink(chatId, linkId);
        tagRepository.add(chatLinkId, tags);

        tagRepository.remove("java");

        List<String> savedTags = tagRepository.findAllByChatLink(chatId, linkId);
        Assertions.assertThat(savedTags).doesNotContain("java");
    }
}
