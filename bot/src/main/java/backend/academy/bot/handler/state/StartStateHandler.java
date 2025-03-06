package backend.academy.bot.handler.state;

import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartStateHandler implements StateHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartStateHandler.class);

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

        logger.info("user [{}] registered successfully", chatId);
        return new SendMessage(chatId, BotMessages.START_HANDLE);
    }
}
