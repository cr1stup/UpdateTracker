package backend.academy.bot.handler.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import java.util.StringTokenizer;

public interface Command {
    String name();

    String description();

    void setState(Update update);

    default boolean isSupport(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String text = update.message().text().trim();
            StringTokenizer tokenizer = new StringTokenizer(text, " ");
            return tokenizer.hasMoreTokens() && tokenizer.nextToken().equals(name());
        }

        return false;
    }

    default BotCommand toApiCommand() {
        return new BotCommand(name(), description());
    }
}
