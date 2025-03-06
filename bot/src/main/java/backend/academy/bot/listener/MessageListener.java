package backend.academy.bot.listener;

import backend.academy.bot.executor.RequestExecutor;
import backend.academy.bot.processor.CommandProcessor;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener implements UpdatesListener {

    private final RequestExecutor requestExecutor;
    private final CommandProcessor commandProcessor;

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendMessage sendMessage = commandProcessor.process(update);
            if (sendMessage != null) {
                requestExecutor.execute(sendMessage
                        .parseMode(ParseMode.Markdown)
                        .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)));
            }
        });

        return CONFIRMED_UPDATES_ALL;
    }
}
