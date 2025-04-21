package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartStateHandler implements StateHandler {

    private final BotService botService;

    @Override
    public BotState state() {
        return BotState.START;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();

        if (!BotMessages.START_NAME.equals(update.message().text())) {
            return new SendMessage(chatId, BotMessages.UNKNOWN_COMMAND);
        }

        var response = botService.registerUser(chatId);
        if (response != null && response.isError()) {
            return new SendMessage(chatId, response.getErrorMessage());
        }

        log.info("user [{}] registered successfully", chatId);
        return new SendMessage(chatId, BotMessages.START_HANDLE);
    }
}
