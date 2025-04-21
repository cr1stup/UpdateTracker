package backend.academy.bot.service;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.executor.RequestExecutor;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class DefaultLinkNotificationService implements LinkNotificationService {

    private final RequestExecutor requestExecutor;
    private final BotService botService;

    @Override
    public void notifyLinkUpdate(LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().forEach(chatId -> {
            var listLinks = botService.getAllLinks(chatId).answer();

            if (linkUpdate.isBatch()) {
                requestExecutor.execute(
                        new SendMessage(chatId, "Батчинг обновлений: %n%n%s".formatted(linkUpdate.description()))
                                .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)));
                return;
            }

            try {
                LinkResponse userLink = listLinks.links().stream()
                        .filter(response -> response.url().equals(linkUpdate.url()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Ссылка не найдена"));

                requestExecutor.execute(new SendMessage(
                                chatId,
                                "%s: %s %ntags: %s %nfilters: %s%ninfo: %s"
                                        .formatted(
                                                BotMessages.UPDATE_MESSAGE,
                                                linkUpdate.url(),
                                                userLink.tags(),
                                                userLink.filters(),
                                                linkUpdate.description()))
                        .linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)));

            } catch (RuntimeException e) {
                requestExecutor.execute(new SendMessage(chatId, e.getMessage()));
            }
        });
    }
}
