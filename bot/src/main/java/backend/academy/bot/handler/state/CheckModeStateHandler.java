package backend.academy.bot.handler.state;

import backend.academy.bot.dto.ChatMode;
import backend.academy.bot.handler.state.model.Mode;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckModeStateHandler implements StateHandler {

    private final BotService botService;
    private final BotRepository botRepository;

    @Override
    public BotState state() {
        return BotState.CHECK_MODE;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        var currentMode = botService.getChatMode(chatId).answer();
        botRepository.setState(chatId, BotState.INPUT_MODE);
        return new SendMessage(chatId, getMessage(currentMode));
    }

    private String getMessage(ChatMode currentMode) {
        StringBuilder instructions = new StringBuilder();

        instructions.append("Текущий режим: ").append(currentMode.name()).append("%n");
        if (currentMode.name().equals(Mode.DAILY.modeName())) {
            instructions.append("Время  отправки: ").append(currentMode.time()).append("%n%n");
        }
        instructions.append(Mode.getInstructionsOfInputMode());

        return instructions.toString().formatted();
    }
}
