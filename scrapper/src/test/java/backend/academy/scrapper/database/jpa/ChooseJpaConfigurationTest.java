package backend.academy.scrapper.database.jpa;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import backend.academy.scrapper.database.IntegrationEnvironment;
import backend.academy.scrapper.service.chat.ChatService;
import backend.academy.scrapper.service.chat.jpa.JpaChatService;
import backend.academy.scrapper.service.link.LinkService;
import backend.academy.scrapper.service.link.jpa.JpaLinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "app.database-access-type=jpa")
class ChooseJpaConfigurationTest extends IntegrationEnvironment {

    @Autowired
    private ChatService chatService;

    @Autowired
    private LinkService linkService;

    @Test
    @DisplayName("chat service is jpa when config jpa")
    void testChatServiceIsJpa() {
        assertThat(chatService).isInstanceOf(JpaChatService.class);
    }

    @Test
    @DisplayName("link service is jpa when config jpa")
    void testLinkServiceIsJpa() {
        assertThat(linkService).isInstanceOf(JpaLinkService.class);
    }
}
