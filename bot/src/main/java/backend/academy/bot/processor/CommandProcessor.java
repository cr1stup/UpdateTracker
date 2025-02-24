package backend.academy.bot.processor;

import backend.academy.bot.handler.command.Command;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;

public interface CommandProcessor {
    List<Command> commands();

    SendMessage process(Update update);
}
