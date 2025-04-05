package backend.academy.scrapper.database.jdbc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import backend.academy.scrapper.database.IntegrationEnvironment;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.jdbc.JdbcChatService;
import backend.academy.scrapper.service.jdbc.JdbcLinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "app.database-access-type=jdbc")
class ChooseJdbcConfigurationTest extends IntegrationEnvironment {

    @Autowired
    private ChatService chatService;

    @Autowired
    private LinkService linkService;

    @Test
    @DisplayName("chat service is jdbc when config jdbc")
    void testChatServiceIsJdbc() {
        assertThat(chatService).isInstanceOf(JdbcChatService.class);
    }

    @Test
    @DisplayName("link service is jdbc when config jdbc")
    void testLinkServiceIsJdbc() {
        assertThat(linkService).isInstanceOf(JdbcLinkService.class);
    }
}
