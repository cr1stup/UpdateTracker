package backend.academy.scrapper.database;

import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.repository.jdbc.JdbcChatLinkRepository;
import backend.academy.scrapper.repository.jdbc.JdbcChatRepository;
import backend.academy.scrapper.repository.jdbc.JdbcLinkRepository;
import backend.academy.scrapper.repository.jdbc.JdbcTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.time.OffsetDateTime;

@SpringBootTest
@TestPropertySource(properties = "app.database-access-type=jdbc")
public class JdbcTestUtil extends IntegrationEnvironment {

    @Autowired
    protected JdbcLinkRepository linkRepository;

    @Autowired
    protected JdbcChatRepository chatRepository;

    @Autowired
    protected JdbcChatLinkRepository chatLinkRepository;

    @Autowired
    protected JdbcTagRepository tagRepository;

    protected Long addLink() {
        var link = Link.create("test.com", "test", OffsetDateTime.MIN, OffsetDateTime.MAX);
        return linkRepository.add(link);
    }

    protected void addChat(Long chatId) {
        chatRepository.add(chatId);
    }

    protected Long addChatLink(long chatId, long linkId) {
        return chatLinkRepository.add(chatId, linkId);
    }
}
