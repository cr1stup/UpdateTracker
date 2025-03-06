package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {

    ListLinksResponse getListLinks(Long tgChatId);

    LinkResponse addLink(AddLinkRequest request, Long tgChatId);

    LinkResponse removeLink(URI link, Long tgChatId);

    List<Link> getListLinkToCheck(Duration after);

    List<Long> getListOfChatId(Link link);

    void update(String url, OffsetDateTime lastModified);

    void checkNow(String url);
}
