package backend.academy.bot.handler.state;

import backend.academy.bot.service.BotService;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListStateHandler implements StateHandler {

    private final BotService botService;

    @Override
    public BotState state() {
        return BotState.LIST;
    }

    @Override
    public SendMessage handle(Update update) {
        var response = botService.getAllLinks(update.message().chat().id()).answer();

        if (response.links() == null || response.links().isEmpty()) {
            return new SendMessage(update.message().chat().id(), "Список отслеживаемых ссылок пуст");
        }

        StringBuilder linksMessage = new StringBuilder();
        linksMessage.append("Список отслеживаемых ссылок:").append("\n");

        int id = 1;
        for (var link : response.links()) {
            linksMessage.append(id).append(". ").append(link.url()).append("\n");
            id++;
        }

        return new SendMessage(update.message().chat().id(), linksMessage.toString());
    }
}
