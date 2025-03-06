package backend.academy.bot.handler.state;

import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.util.ChatLimits;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagStateHandler implements StateHandler {

    private final BotRepository botRepository;
    private static final int MAX_TAGS = ChatLimits.MAX_TAGS;
    private static final int MAX_FILTERS = ChatLimits.MAX_FILTERS;

    @Override
    public BotState state() {
        return BotState.TAG;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String[] userText = update.message().text().split(" ");

        if (userText.length > MAX_TAGS) {
            return new SendMessage(chatId, "Введите меньше %d тегов".formatted(MAX_TAGS));
        } else if (!"null".equalsIgnoreCase(userText[0])) {
            botRepository.setTags(chatId, Arrays.asList(userText));
        }

        botRepository.setState(chatId, BotState.FILTER);
        return new SendMessage(
                chatId,
                "Введите фильтры (не больше %d) через пробел или \"null\", если хотите без них".formatted(MAX_FILTERS));
    }
}
