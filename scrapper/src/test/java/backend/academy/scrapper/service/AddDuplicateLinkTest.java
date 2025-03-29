package backend.academy.scrapper.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.exception.LinkAlreadyAddedException;
import backend.academy.scrapper.repository.memory.DefaultChatRepository;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddDuplicateLinkTest {

    @Mock
    private DefaultChatRepository chatRepository;

    @InjectMocks
    private DefaultLinkService linkService;

    @Test
    @DisplayName("throw exception when adding duplicate link")
    void testDuplicateAddLink() {
        Long tgChatId = 123L;
        URI url = URI.create("https://example.com");
        AddLinkRequest request = new AddLinkRequest(url, List.of("1", "2", "3"), List.of("1", "2", "3"));
        when(chatRepository.isChatExist(tgChatId)).thenReturn(true);
        when(chatRepository.isLinkByChatExist(tgChatId, url)).thenReturn(true);

        Assertions.assertThrows(LinkAlreadyAddedException.class, () -> linkService.addLink(request, tgChatId));
        verify(chatRepository).isChatExist(tgChatId);
        verify(chatRepository).isLinkByChatExist(tgChatId, url);
    }
}
