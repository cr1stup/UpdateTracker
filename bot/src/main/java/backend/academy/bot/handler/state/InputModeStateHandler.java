package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.Mode;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InputModeStateHandler implements StateHandler {

    private final BotService botService;
    private final BotRepository botRepository;
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    @Override
    public BotState state() {
        return BotState.INPUT_MODE;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String[] userText = update.message().text().split(" ");
        var currentMode = botService.getChatMode(chatId).answer();

        if (userText.length == 1 && Mode.IMMEDIATE.modeName().equalsIgnoreCase(userText[0])) {
            if (currentMode.name().equalsIgnoreCase(Mode.IMMEDIATE.modeName())) {
                return new SendMessage(chatId, "У вас уже установлен данный режим");
            } else {
                var response = botService.setChatMode(chatId, Mode.IMMEDIATE.modeName(), null);
                if (response != null && response.isError()) {
                    return new SendMessage(chatId, response.getErrorMessage());
                }
                botRepository.setState(chatId, BotState.START);
                return new SendMessage(chatId, BotMessages.SETMODE_SUCCESS);
            }
        }

        if (userText.length == 2
            && Mode.DAILY.modeName().equalsIgnoreCase(userText[0])
            && TIME_PATTERN.matcher(userText[1]).matches()
        ) {
            var response = botService.setChatMode(
                chatId, Mode.DAILY.modeName(), LocalTime.parse(userText[1], DateTimeFormatter.ofPattern("H:mm")));
            if (response != null && response.isError()) {
                return new SendMessage(chatId, response.getErrorMessage());
            }
            botRepository.setState(chatId, BotState.START);
            return new SendMessage(chatId, BotMessages.SETMODE_SUCCESS);
        }

        return new SendMessage(chatId, Mode.getInstructionsOfInputMode());
    }
}
