package backend.academy.bot;

import backend.academy.bot.executor.RequestExecutor;
import backend.academy.bot.handler.command.Command;
import backend.academy.bot.listener.MessageListener;
import backend.academy.bot.processor.CommandProcessor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public final class LinkTrackerBot implements AutoCloseable {

    private final TelegramBot telegramBot;
    private final RequestExecutor requestExecutor;
    private final CommandProcessor commandProcessor;
    private final MessageListener messageListener;

    @PostConstruct
    public void start() {
        var commands = new SetMyCommands(commandProcessor.commands().stream()
                .map(Command::toApiCommand)
                .toList()
                .toArray(new BotCommand[0]));

        requestExecutor.execute(commands);

        telegramBot.setUpdatesListener(messageListener);
        log.info("Launching Telegram bot");
    }

    @Override
    public void close() {
        telegramBot.shutdown();
        log.info("Closing Telegram Bot");
    }
}
