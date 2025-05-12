package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListStateHandler implements StateHandler {

    private final BotService botService;
    private final BotRepository botRepository;

    @Override
    public BotState state() {
        return BotState.LIST;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        botRepository.setState(chatId, BotState.START);

        var response = botService.getAllLinks(chatId);
        if (response.isError()) {
            return new SendMessage(chatId, response.getErrorMessage());
        }

        var listLinks = response.answer();

        if (listLinks.links() == null || listLinks.links().isEmpty()) {
            log.info("user [{}] list is empty", chatId);
            return new SendMessage(chatId, BotMessages.EMPTY_LIST);
        }

        StringBuilder linksMessage = new StringBuilder();
        linksMessage.append("Список отслеживаемых ссылок:").append("%n");

        int id = 1;
        for (var link : listLinks.links()) {
            linksMessage.append(id).append(". ").append(link.url()).append("%n");
            id++;
        }

        log.info("user [{}] get list links successfully", chatId);
        return new SendMessage(chatId, linksMessage.toString().formatted());
    }
}
