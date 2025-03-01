package backend.academy.bot.handler.state;

import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import backend.academy.bot.util.LinkUtil;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackStateHandler implements StateHandler {

    private final BotService botService;

    @Override
    public BotState state() {
        return BotState.TRACK;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String[] userText = update.message().text().split(" ");
        String link = LinkUtil.getLink(userText);

        if (link == null && BotMessages.TRACK_NAME.equals(userText[0])) {
            return new SendMessage(chatId, "Введите ссылку для отслеживания");
        } else if (link == null) {
            return new SendMessage(chatId, "Введите корректную ссылку");
        }

        var response = botService.linkUrlToUser(link, chatId);

        if (response.isError()) {
            return new SendMessage(chatId, "Не удалось сохранить ссылку: " + response.getErrorMessage());
        }

        return new SendMessage(chatId, "Ваша сслыка успешно сохранена для отслеживания!");
    }
}
