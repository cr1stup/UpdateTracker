package backend.academy.scrapper.database.jdbc;

import backend.academy.scrapper.database.JdbcTestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JdbcChatRepositoryTest extends JdbcTestUtil {

    @Test
    @Transactional
    @Rollback
    @DisplayName("add insert chat in database")
    void testAddChatInDatabase() {
        chatRepository.add(123L);

        Assertions.assertThat(chatRepository.findAll()).contains(123L);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("remove delete chat from database")
    void testDeleteChatFromDatabase() {
        chatRepository.add(123L);

        chatRepository.remove(123L);

        Assertions.assertThat(chatRepository.findAll()).doesNotContain(123L);
    }
}
