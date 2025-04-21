package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.util.BotMessages;
import backend.academy.bot.util.ChatLimits;
import backend.academy.bot.util.LinkUtil;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackStateHandler implements StateHandler {

    private final BotRepository botRepository;
    private static final int MAX_TAGS = ChatLimits.MAX_TAGS;

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

        botRepository.setLink(chatId, link);
        botRepository.setState(chatId, BotState.TAG_INPUT);

        return new SendMessage(
                chatId,
                "Введите тэги (не больше %d) через пробел или \"null\", если хотите без них".formatted(MAX_TAGS));
    }
}
