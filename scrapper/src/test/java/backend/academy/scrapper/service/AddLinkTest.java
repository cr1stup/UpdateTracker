package backend.academy.scrapper.service;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddLinkTest {

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

    @Test
    @DisplayName("correct saving data to repository")
    void shouldSaveCorrectData_WhenAddingNewLink() {
        AddLinkRequest request = createAddLinkRequest();
        LinkClient client = mock(LinkClient.class);
        LinkInformation linkInformation = createLinkInformation();
        mockRepositories(client, linkInformation);

        LinkResponse response = linkService.addLink(request, TG_CHAT_ID);

        assertThat(response).isNotNull();
        assertThat(response.url()).isEqualTo(URL);
        assertThat(response.tags()).containsExactlyElementsOf(request.tags());
        assertThat(response.filters()).containsExactlyElementsOf(request.filters());
        verifyMocks(client);
    }

    private AddLinkRequest createAddLinkRequest() {
        return new AddLinkRequest(URL, List.of("tag1", "tag2"), List.of("filter1"));
    }

    private LinkInformation createLinkInformation() {
        return new LinkInformation(URL, "example/repo", "Description", OffsetDateTime.now());
    }

    private void mockRepositories(LinkClient client, LinkInformation linkInformation) {
        when(chatRepository.isChatExist(TG_CHAT_ID)).thenReturn(true);
        when(chatRepository.isLinkByChatExist(TG_CHAT_ID, URL)).thenReturn(false);
        when(clientFactory.getClient(URL)).thenReturn(client);
        when(client.fetchInformation(URL)).thenReturn(linkInformation);
        when(linkRepository.addChatByLink(Mockito.anyLong(), Mockito.any(Link.class))).thenReturn(123L);
    }

    private void verifyMocks(LinkClient client) {
        verify(chatRepository).isChatExist(TG_CHAT_ID);
        verify(chatRepository).isLinkByChatExist(TG_CHAT_ID, URL);
        verify(clientFactory).getClient(URL);
        verify(client).fetchInformation(URL);
        verify(linkRepository).addChatByLink(Mockito.eq(TG_CHAT_ID), Mockito.any(Link.class));
        verify(chatRepository).addLinkByChat(Mockito.eq(TG_CHAT_ID), Mockito.any(Link.class));
    }
}
