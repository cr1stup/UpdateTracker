package backend.academy.bot.handler.state;

import backend.academy.bot.handler.command.Command;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartStateHandler implements StateHandler {

    private final BotService botService;
    private final Command startCommand;

    @Override
    public BotState state() {
        return BotState.START;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();

        if (!startCommand.isSupport(update)) {
            return new SendMessage(chatId, BotMessages.UNKNOWN_COMMAND);
        }

        var response = botService.registerUser(chatId);
        if (response != null && response.isError()) {
            return new SendMessage(chatId, "Ошибка регистрации чата: " + response.getErrorMessage());
        }

        return new SendMessage(chatId, BotMessages.START_HANDLE);
    }
}
