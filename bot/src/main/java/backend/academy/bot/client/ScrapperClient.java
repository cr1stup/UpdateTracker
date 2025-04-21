package backend.academy.bot.client;

import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.ChatMode;
import backend.academy.bot.dto.OptionalAnswer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ScrapperClient {

    @PostExchange("/tg-chat/{id}")
    OptionalAnswer<Void> registerChat(@PathVariable Long id);

    @DeleteExchange("/tg-chat/{id}")
    OptionalAnswer<Void> deleteChat(@PathVariable Long id);

    @PostExchange("/mode")
    OptionalAnswer<Void> setChatMode(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody ChatMode chatMode);

    @GetExchange("mode")
    OptionalAnswer<ChatMode> getChatMode(@RequestHeader("Tg-Chat-Id") Long id);

    @GetExchange("/links")
    OptionalAnswer<ListLinksResponse> listLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId);

    @PostExchange("/links")
    OptionalAnswer<LinkResponse> addLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody AddLinkRequest addLinkRequest);

    @DeleteExchange("/links")
    OptionalAnswer<LinkResponse> removeLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody RemoveLinkRequest removeLinkRequest);
}
