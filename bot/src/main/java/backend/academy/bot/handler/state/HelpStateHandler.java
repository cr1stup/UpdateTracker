package backend.academy.bot.handler.state;

import backend.academy.bot.handler.command.Command;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpStateHandler implements StateHandler {

    private final List<Command> commands;
    private final Command helpCommand;

    @Override
    public BotState state() {
        return BotState.HELP;
    }

    @Override
    public SendMessage handle(Update update) {
        if (!helpCommand.isSupport(update)) {
            return new SendMessage(update.message().chat().id(), BotMessages.UNKNOWN_COMMAND);
        }

        StringBuilder message = new StringBuilder();
        commands.forEach(command -> message.append(command.name())
                .append(" ")
                .append(command.description())
                .append(String.format("%n")));

        return new SendMessage(update.message().chat().id(), message.toString());
    }
}
