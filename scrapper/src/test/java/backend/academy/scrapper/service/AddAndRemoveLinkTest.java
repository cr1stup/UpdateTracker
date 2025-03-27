package backend.academy.scrapper.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.repository.DefaultChatRepository;
import backend.academy.scrapper.repository.DefaultLinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddAndRemoveLinkTest {

    @Mock
    private DefaultChatRepository chatRepository;

    @Mock
    private DefaultLinkRepository linkRepository;

    @Mock
    private ClientFactory clientFactory;

    @InjectMocks
    private DefaultLinkService linkService;

    private static final Long TG_CHAT_ID = 1L;
    private static final URI URL = URI.create("https://github.com/example/repo");
    private static final List<String> TAGS = List.of("tag1", "tag2");
    private static final List<String> FILTERS = List.of("filter1");

    @Test
    @DisplayName("happy path adding link")
    void testAddLinkSuccess() {
        mockAddLinkBehavior();

        LinkResponse response = linkService.addLink(createAddRequest(), TG_CHAT_ID);

        assertAddResponse(response);
    }

    @Test
    @DisplayName("happy path removing link")
    void testRemoveLinkSuccess() {
        mockRemoveLinkBehavior();

        LinkResponse response = linkService.removeLink(URL, TG_CHAT_ID);

        assertRemoveResponse(response);
    }

    private void assertAddResponse(LinkResponse response) {
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(123L);
        assertThat(response.url()).isEqualTo(URL);
        assertThat(response.tags()).containsExactlyElementsOf(TAGS);
        assertThat(response.filters()).containsExactlyElementsOf(FILTERS);

        verify(chatRepository).isChatExist(TG_CHAT_ID);
        verify(chatRepository).isLinkByChatExist(TG_CHAT_ID, URL);
        verify(clientFactory).getClient(URL);
        verify(linkRepository).addChatByLink(eq(TG_CHAT_ID), any(Link.class));
        verify(chatRepository).addLinkByChat(eq(TG_CHAT_ID), any(Link.class));
    }

    private void assertRemoveResponse(LinkResponse response) {
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(123L);
        assertThat(response.url()).isEqualTo(URL);
        assertThat(response.tags()).containsExactlyElementsOf(TAGS);
        assertThat(response.filters()).containsExactlyElementsOf(FILTERS);

        verify(chatRepository).deleteLinkByChat(TG_CHAT_ID, URL);
        verify(linkRepository).removeChatByLink(TG_CHAT_ID, URL);
    }

    private void mockAddLinkBehavior() {
        LinkClient client = mock(LinkClient.class);
        LinkInformation linkInfo = new LinkInformation(URL, "example/repo", "Description", OffsetDateTime.now());

        when(chatRepository.isChatExist(TG_CHAT_ID)).thenReturn(true);
        when(chatRepository.isLinkByChatExist(TG_CHAT_ID, URL)).thenReturn(false);
        when(clientFactory.getClient(URL)).thenReturn(client);
        when(client.fetchInformation(URL)).thenReturn(linkInfo);
        when(linkRepository.addChatByLink(eq(TG_CHAT_ID), any(Link.class))).thenReturn(123L);
    }

    private void mockRemoveLinkBehavior() {
        Link link = new Link(123L, URL.toString(), TAGS, FILTERS, OffsetDateTime.now(), OffsetDateTime.now());
        when(linkRepository.removeChatByLink(TG_CHAT_ID, URL)).thenReturn(link);
    }

    private AddLinkRequest createAddRequest() {
        return new AddLinkRequest(URL, TAGS, FILTERS);
    }
}
