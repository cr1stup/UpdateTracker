package backend.academy.bot.processor;

import backend.academy.bot.handler.command.Command;
import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.handler.state.StateHandler;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultCommandProcessor implements CommandProcessor {

    private final List<Command> commands;
    private final List<StateHandler> stateHandlers;
    private final BotRepository botRepository;

    @Override
    public List<Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        if (update.message() == null) {
            return null;
        }

        for (Command command : commands) {
            if (command.isSupport(update)) {
                command.setState(update);
            }
        }

        long chatId = update.message().chat().id();
        BotState currentState = botRepository.getState(chatId);

        for (StateHandler stateHandler : stateHandlers) {
            if (stateHandler.isSupport(currentState)) {
                return stateHandler.handle(update);
            }
        }

        return new SendMessage(chatId, BotMessages.UNKNOWN_COMMAND);
    }
}
