package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.ApiErrorResponse;
import backend.academy.bot.dto.ChatMode;
import backend.academy.bot.dto.OptionalAnswer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultBotService implements BotService {

    private final ScrapperClient scrapperClient;

    @Override
    @CircuitBreaker(name = "botService", fallbackMethod = "fallback")
    public OptionalAnswer<Void> registerUser(Long id) {
        return scrapperClient.registerChat(id);
    }

    @Override
    @CircuitBreaker(name = "botService", fallbackMethod = "fallback")
    public OptionalAnswer<LinkResponse> linkUrlToUser(
            String url, Long userId, List<String> tags, List<String> filters) {
        return scrapperClient.addLink(userId, new AddLinkRequest(URI.create(url), tags, filters));
    }

    @Override
    @CircuitBreaker(name = "botService", fallbackMethod = "fallback")
    public OptionalAnswer<LinkResponse> unlinkUrlFromUser(URI link, Long userId) {
        return scrapperClient.removeLink(userId, new RemoveLinkRequest(link));
    }

    @Override
    @CircuitBreaker(name = "botService", fallbackMethod = "fallback")
    public OptionalAnswer<ListLinksResponse> getAllLinks(Long userId) {
        return scrapperClient.listLinks(userId);
    }

    @Override
    @CircuitBreaker(name = "botService", fallbackMethod = "fallback")
    public OptionalAnswer<Void> setChatMode(Long id, String mode, LocalTime time) {
        return scrapperClient.setChatMode(id, new ChatMode(id, mode, time));
    }

    @Override
    @CircuitBreaker(name = "botService", fallbackMethod = "fallback")
    public OptionalAnswer<ChatMode> getChatMode(Long id) {
        return scrapperClient.getChatMode(id);
    }

    @SuppressWarnings("unused")
    public OptionalAnswer<Void> fallback(Long id, Exception e) {
        log.error("Fallback after retries failed, user {}, message: {}", id, e.getMessage());

        return OptionalAnswer.error(new ApiErrorResponse(
            "Scrapper service is unavailable or unresponsive",
            "503",
            e.getClass().getSimpleName(),
            "Сервис обработки запросов недоступен, повторите попытку позже",
            List.of())
        );
    }
}
