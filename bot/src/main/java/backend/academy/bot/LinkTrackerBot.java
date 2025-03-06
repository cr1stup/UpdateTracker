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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkTrackerBot implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(LinkTrackerBot.class);

    private final TelegramBot telegramBot;
    private final RequestExecutor requestExecutor;
    private final CommandProcessor commandProcessor;
    private final MessageListener messageListener;

    @PostConstruct
    public void start() {
        requestExecutor.execute(new SetMyCommands(commandProcessor.commands().stream()
                .map(Command::toApiCommand)
                .toList()
                .toArray(new BotCommand[0])));

        telegramBot.setUpdatesListener(messageListener);
        logger.info("Launching Telegram bot");
    }

    @Override
    public void close() {
        telegramBot.shutdown();
        logger.info("Closing Telegram Bot");
    }
}
