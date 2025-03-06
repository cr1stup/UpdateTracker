package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import java.net.URI;

public interface LinkService {

    ListLinksResponse getListLinks(Long tgChatId);

    LinkResponse addLink(AddLinkRequest request, Long tgChatId);

    LinkResponse removeLink(URI link, Long tgChatId);
}
