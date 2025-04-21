package backend.academy.bot.command;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.OptionalAnswer;
import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.handler.state.ListStateHandler;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListCommandViewTest {

    @Mock
    private BotService botService;

    @Mock
    private BotRepository botRepository;

    @InjectMocks
    private ListStateHandler listStateHandler;

    private Update updateMock;
    private final Long chatId = 12345L;

    @BeforeEach
    void setUp() {
        updateMock = mock(Update.class);
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);

        when(updateMock.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(chatId);
    }

    @Test
    @DisplayName("return empty list message when no links are tracked")
    void testTrackedListIsEmpty() {
        ListLinksResponse emptyResponse = new ListLinksResponse(List.of(), 0);
        OptionalAnswer<ListLinksResponse> optionalAnswer = OptionalAnswer.of(emptyResponse);
        when(botService.getAllLinks(chatId)).thenReturn(optionalAnswer);

        SendMessage result = listStateHandler.handle(updateMock);

        assertThat(BotMessages.EMPTY_LIST).isEqualTo(result.getParameters().get("text"));
        verify(botRepository).setState(chatId, BotState.START);
    }

    @Test
    @DisplayName("return formatted list of tracked links")
    void testTrackedListWithLinks() {
        ListLinksResponse response = new ListLinksResponse(
                List.of(
                        new LinkResponse(1L, URI.create("https://example.com/1"), List.of(), List.of()),
                        new LinkResponse(2L, URI.create("https://example.com/2"), List.of(), List.of())),
                2);
        OptionalAnswer<ListLinksResponse> optionalAnswer = OptionalAnswer.of(response);
        when(botService.getAllLinks(chatId)).thenReturn(optionalAnswer);
        String expectedMessage =
                "Список отслеживаемых ссылок:%n1. https://example.com/1%n2. https://example.com/2%n".formatted();

        SendMessage result = listStateHandler.handle(updateMock);

        assertThat(expectedMessage).isEqualTo(result.getParameters().get("text"));
        verify(botRepository).setState(chatId, BotState.START);
    }
}
