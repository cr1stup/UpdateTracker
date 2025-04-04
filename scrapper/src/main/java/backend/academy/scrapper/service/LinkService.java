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

    ListLinksResponse getListLinks(Long chatId);

    LinkResponse addLink(AddLinkRequest request, Long chatId);

    LinkResponse removeLink(URI link, Long chatId);

    List<Link> getListLinkToCheck(Duration after, int limit);

    List<Long> getListOfChatId(Long linkId);

    void update(Long linkId, OffsetDateTime lastModified);

    void checkNow(Long linkId);
}
