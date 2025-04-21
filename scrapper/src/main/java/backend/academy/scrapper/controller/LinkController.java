package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.link.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/links", consumes = "application/json", produces = "application/json")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @GetMapping
    @Cacheable(value = "user-links", key = "#chatId")
    public ListLinksResponse listLinks(@RequestHeader(name = "Tg-Chat-Id") Long chatId) {
        return linkService.getListLinks(chatId);
    }

    @PostMapping
    @CacheEvict(value = "user-links", key = "#chatId")
    public LinkResponse addLink(
            @RequestHeader(name = "Tg-Chat-Id") Long chatId, @RequestBody @Valid AddLinkRequest addLinkRequest) {
        return linkService.addLink(addLinkRequest, chatId);
    }

    @DeleteMapping
    @CacheEvict(value = "user-links", key = "#chatId")
    public LinkResponse removeLink(
            @RequestHeader(name = "Tg-Chat-Id") Long chatId,
            @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        return linkService.removeLink(removeLinkRequest.link(), chatId);
    }
}
