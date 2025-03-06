package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ListLinksResponse listLinks(@RequestHeader(name = "Tg-Chat-Id") Long tgChatId) {
        return linkService.getListLinks(tgChatId);
    }

    @PostMapping
    public LinkResponse addLink(
        @RequestHeader(name = "Tg-Chat-Id") Long tgChatId,
        @RequestBody @Valid AddLinkRequest addLinkRequest
    ) {
        return linkService.addLink(addLinkRequest, tgChatId);
    }

    @DeleteMapping
    public LinkResponse removeLink(
        @RequestHeader(name = "Tg-Chat-Id") Long tgChatId,
        @RequestBody @Valid RemoveLinkRequest addLinkRequest
    ) {
        return linkService.removeLink(addLinkRequest.link(), tgChatId);
    }
}
