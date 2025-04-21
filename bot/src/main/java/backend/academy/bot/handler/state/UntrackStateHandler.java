package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import backend.academy.bot.util.LinkUtil;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UntrackStateHandler implements StateHandler {

    private final BotService botService;
    private final BotRepository botRepository;

    @Override
    public BotState state() {
        return BotState.UNTRACK;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();

        var listLinks = botService.getAllLinks(chatId).answer();
        if (listLinks.links() == null || listLinks.links().isEmpty()) {
            botRepository.setState(chatId, BotState.START);
            log.info("user [{}] list is empty", chatId);
            return new SendMessage(chatId, BotMessages.EMPTY_LIST);
        }

        String[] userText = update.message().text().split(" ");
        String link = LinkUtil.getLink(userText);

        if (link == null && BotMessages.UNTRACK_NAME.equals(userText[0])) {
            return new SendMessage(chatId, "Введите ссылку для удаления из отслеживаемых");
        } else if (link == null) {
            return new SendMessage(chatId, "Введите корректную ссылку");
        }

        var response = botService.unlinkUrlFromUser(URI.create(link), chatId);
        botRepository.setState(chatId, BotState.START);

        if (response.isError()) {
            return new SendMessage(chatId, "Не удалось удалить ссылку: " + response.getErrorMessage());
        }

        log.info("user [{}] remove link successfully", chatId);
        return new SendMessage(chatId, "Ваша сслыка успешно удалена из отслеживаемых!");
    }
}
