package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.ChatLimits;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilterInputStateHandler implements StateHandler {

    private final BotService botService;
    private final BotRepository botRepository;
    private static final int MAX_FILTERS = ChatLimits.MAX_FILTERS;

    @Override
    public BotState state() {
        return BotState.FILTER_INPUT;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String[] userText = update.message().text().split(" ");

        if (userText.length > MAX_FILTERS) {
            return new SendMessage(chatId, "Введите меньше %d фильтров".formatted(MAX_FILTERS));
        }

        if (!"null".equalsIgnoreCase(userText[0])) {
            botRepository.setFilters(chatId, Arrays.asList(userText));
        }

        var response = botService.linkUrlToUser(
                botRepository.getLink(chatId), chatId, botRepository.getTags(chatId), botRepository.getFilters(chatId));

        if (response.isError()) {
            return new SendMessage(chatId, "Не удалось сохранить ссылку: " + response.getErrorMessage());
        }

        botRepository.setState(chatId, BotState.START);

        log.info("user [{}] save link successfully", chatId);
        return new SendMessage(chatId, "Ваша ссылка успешно сохранена для отслеживания!");
    }
}
