package backend.academy.scrapper.database.jdbc;

import backend.academy.scrapper.database.JdbcTestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JdbcChatLinkRepositoryTest extends JdbcTestUtil {

    @Test
    @Transactional
    @Rollback
    @DisplayName("add insert chat_link in database")
    void testAddLinkInDatabase() {
        long chatId = 123L;
        long linkId = addLink();
        addChat(chatId);

        chatLinkRepository.add(chatId, linkId);

        Assertions.assertThat(chatLinkRepository.findAllByLinkId(linkId)).contains(chatId);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("remove delete chat_link from database")
    void testDeleteLinkFromDatabase() {
        long chatId = 123L;
        long linkId = addLink();
        addChat(chatId);
        chatLinkRepository.add(chatId, linkId);

        chatLinkRepository.remove(chatId, linkId);

        Assertions.assertThat(chatLinkRepository.findAllByLinkId(linkId)).doesNotContain(chatId);
    }
}
