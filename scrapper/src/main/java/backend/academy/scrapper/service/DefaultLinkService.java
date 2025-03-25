package backend.academy.scrapper.service;

import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.LinkAlreadyAddedException;
import backend.academy.scrapper.exception.LinkIsNotSupportedException;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultLinkService implements LinkService {

    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final ClientFactory clientFactory;

    @Override
    public ListLinksResponse getListLinks(Long tgChatId) {
        var links = chatRepository.getLinksByChat(tgChatId);

        var linkResponses = links.stream()
                .map(link -> new LinkResponse(link.id(), URI.create(link.url()), link.tags(), link.filters()))
                .toList();

        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    @Override
    @Transactional
    public LinkResponse addLink(AddLinkRequest request, Long tgChatId) {
        if (!chatRepository.isChatExist(tgChatId)) {
            log.info("user [{}] not save link: chat not found", tgChatId);
            throw new ChatNotFoundException();
        }

        URI url = request.link();
        if (chatRepository.isLinkByChatExist(tgChatId, url)) {
            log.info("user [{}] not save link: link already added", tgChatId);
            throw new LinkAlreadyAddedException(url);
        }

        LinkClient client = clientFactory.getClient(url);
        if (client == null) {
            log.info("user [{}] not save link: link is not supported", tgChatId);
            throw new LinkIsNotSupportedException(url);
        }

        LinkInformation linkInformation = client.fetchInformation(url);
        if (linkInformation == null) {
            log.info("user [{}] not save link: link is not supported", tgChatId);
            throw new LinkIsNotSupportedException(url);
        }

        Link link = new Link(
                0,
                url.toString(),
                request.tags(),
                request.filters(),
                linkInformation.lastModified(),
                OffsetDateTime.now(ZoneId.systemDefault()));

        var id = linkRepository.addChatByLink(tgChatId, link);
        chatRepository.addLinkByChat(tgChatId, link);

        return new LinkResponse(id, url, request.tags(), request.filters());
    }

    @Override
    public LinkResponse removeLink(URI url, Long tgChatId) {
        chatRepository.deleteLinkByChat(tgChatId, url);
        Link link = linkRepository.removeChatByLink(tgChatId, url);
        return new LinkResponse(link.id(), URI.create(link.url()), link.tags(), link.filters());
    }

    @Override
    public List<Link> getListLinkToCheck(Duration after) {
        OffsetDateTime threshold = OffsetDateTime.now(ZoneId.systemDefault()).minus(after);
        var links = linkRepository.getAllLinks();

        return links.stream()
                .filter(link -> link.lastCheckedAt().isBefore(threshold))
                .sorted(Comparator.comparing(Link::lastCheckedAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getListOfChatId(Link link) {
        return linkRepository.getAllChatIdByLink(link).stream().toList();
    }

    @Override
    public void update(String url, OffsetDateTime lastModified) {
        var links = linkRepository.getAllLinks();
        links.stream().filter(link -> url.equals(link.url())).findFirst().ifPresent(link -> {
            link.lastCheckedAt(OffsetDateTime.now(ZoneId.systemDefault()));
            link.updatedAt(lastModified);
        });
    }

    @Override
    public void checkNow(String url) {
        var links = linkRepository.getAllLinks();
        links.stream()
                .filter(link -> url.equals(link.url()))
                .findFirst()
                .ifPresent(link -> link.lastCheckedAt(OffsetDateTime.now(ZoneId.systemDefault())));
    }
}
