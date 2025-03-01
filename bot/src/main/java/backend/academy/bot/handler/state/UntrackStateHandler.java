package backend.academy.bot.handler.state;

import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import backend.academy.bot.util.LinkUtil;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackStateHandler implements StateHandler {

    private final BotService botService;

    @Override
    public BotState state() {
        return BotState.UNTRACK;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();

        var listLinks = botService.getAllLinks(update.message().chat().id()).answer();
        if (listLinks.links().isEmpty()) {
            return new SendMessage(update.message().chat().id(), "Список отслеживаемых ссылок пуст");
        }

        String[] userText = update.message().text().split(" ");
        String link = LinkUtil.getLink(userText);

        if (link == null && BotMessages.UNTRACK_NAME.equals(userText[0])) {
            return new SendMessage(chatId, "Введите ссылку для удаления из отслеживаемых");
        } else if (link == null) {
            return new SendMessage(chatId, "Введите корректную ссылку");
        }

        var response = botService.unlinkUrlFromUser(URI.create(link), chatId);

        if (response.isError()) {
            return new SendMessage(chatId, "Не удалось удалить ссылку: " + response.getErrorMessage());
        }

        return new SendMessage(chatId, "Ваша сслыка успешно удалена из отслеживаемых!");
    }
}
