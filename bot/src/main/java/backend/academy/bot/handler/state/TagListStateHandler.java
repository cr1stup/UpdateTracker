package backend.academy.bot.handler.state;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.service.BotService;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TagListStateHandler implements StateHandler {

    private final BotService botService;

    @Override
    public BotState state() {
        return BotState.TAG_LIST;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String[] userText = update.message().text().split(" ");
        var response = botService.getAllLinks(chatId).answer();

        if (response.links() == null || response.links().isEmpty()) {
            log.info("user [{}] list is empty", chatId);
            return new SendMessage(chatId, BotMessages.EMPTY_LIST);
        }

        if (userText.length == 1 && "all".equalsIgnoreCase(userText[0])) {
            String allTags = response.links().stream()
                .flatMap(link -> link.tags().stream())
                .distinct()
                .collect(Collectors.joining(", "));

            return new SendMessage(chatId, "Ваши теги: " + allTags);
        }

        if ((userText.length == 1 && !BotMessages.TAGLIST_NAME.equals(userText[0]))
            || (userText.length == 2 && BotMessages.TAGLIST_NAME.equals(userText[0])))
        {
            int id = 1;
            boolean found = false;
            String tagName = userText.length == 1? userText[0] : userText[1];

            StringBuilder linksMessage = new StringBuilder();
            linksMessage.append("Список отслеживаемых ссылок по тэгу ").append(tagName).append(":%n");

            for (LinkResponse link : response.links()) {
                if (link.tags().contains(tagName)) {
                    found = true;
                    linksMessage.append(id).append(". ").append(link.url()).append("%n");
                    id++;
                }
            }

            if (!found) {
                return new SendMessage(chatId, "Введите верный тег");
            }

            return new SendMessage(chatId, linksMessage.toString().formatted());
        }

        return new SendMessage(chatId, BotMessages.TAGLIST_GREATING);
    }
}
