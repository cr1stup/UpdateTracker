package backend.academy.bot.command;

import backend.academy.bot.handler.state.StartStateHandler;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UnknownCommandTest {

    @InjectMocks
    private StartStateHandler startHandler;

    @Test
    @DisplayName("bot send special message for unknown command")
    public void testResponseUnknownCommand() {
        Update update = Mockito.mock(Update.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(update.message().text()).thenReturn("/unknown");

        SendMessage botResponse = startHandler.handle(update);

        assertThat(botResponse.getParameters().get("text")).isEqualTo(BotMessages.UNKNOWN_COMMAND);
    }
}
