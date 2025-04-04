package backend.academy.scrapper.service.jdbc;

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
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.jdbc.JdbcChatLinkRepository;
import backend.academy.scrapper.repository.jdbc.JdbcChatRepository;
import backend.academy.scrapper.repository.jdbc.JdbcFilterRepository;
import backend.academy.scrapper.repository.jdbc.JdbcLinkRepository;
import backend.academy.scrapper.repository.jdbc.JdbcTagRepository;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class JdbcLinkService implements LinkService {

    private final ClientFactory clientFactory;
    private final JdbcChatLinkRepository chatLinkRepository;
    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcTagRepository tagRepository;
    private final JdbcFilterRepository filterRepository;

    @Override
    public ListLinksResponse getListLinks(Long chatId) {
        var links = chatLinkRepository.findAllByChatId(chatId);

        var linkResponses = links.stream()
                .map(link -> new LinkResponse(
                    link.id(),
                    URI.create(link.url()),
                    tagRepository.findAllByChatLink(chatId, link.id()),
                    filterRepository.findAllByChatLink(chatId, link.id())))
                .toList();

        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    @Override
    @Transactional
    public LinkResponse addLink(AddLinkRequest request, Long chatId) {
        if (!chatRepository.isExists(chatId)) {
            log.info("user [{}] not save link: chat not found", chatId);
            throw new ChatNotFoundException();
        }

        URI url = request.link();
        if (chatLinkRepository.isLinkAlreadyExist(chatId, url)) {
            log.info("user [{}] not save link: link already added", chatId);
            throw new LinkAlreadyAddedException(url);
        }

        LinkClient client = clientFactory.getClient(url);
        if (client == null) {
            log.info("user [{}] not save link: link is not supported", chatId);
            throw new LinkIsNotSupportedException(url);
        }

        LinkInformation linkInformation = client.fetchInformation(url);
        if (linkInformation == null) {
            log.info("user [{}] not save link: link is not supported", chatId);
            throw new LinkIsNotSupportedException(url);
        }

        var linkId = linkRepository.add(Link.create(
            url.toString(),
            linkInformation.title(),
            linkInformation.events().getFirst().lastModified(),
            OffsetDateTime.now(ZoneId.systemDefault())
        ));

        var chatLinkId = chatLinkRepository.add(chatId, linkId);
        tagRepository.add(chatLinkId, request.tags());
        filterRepository.add(chatLinkId, request.filters());

        return new LinkResponse(linkId, url, request.tags(), request.filters());
    }

    @Override
    @Transactional
    public LinkResponse removeLink(URI url, Long tgChatId) {
        Optional<Link> optionalLink = linkRepository.findByUrl(url.toString());
        if (optionalLink.isPresent()) {
            Link link = optionalLink.get();
            var tags = tagRepository.findAllByChatLink(tgChatId, link.id());
            var filters = filterRepository.findAllByChatLink(tgChatId, link.id());

            chatLinkRepository.remove(tgChatId, link.id());
            if (chatLinkRepository.findAllByLinkId(link.id()).isEmpty()) {
                linkRepository.remove(link.id());
            }

            tags.forEach(tag -> {
                if (tagRepository.findByName(tag).isEmpty()) {
                    tagRepository.remove(tag);
                }
            });

            filters.forEach(filter -> {
                if (filterRepository.findByName(filter).isEmpty()) {
                    filterRepository.remove(filter);
                }
            });

            return new LinkResponse(link.id(), URI.create(link.url()), tags, filters);
        } else {
            throw new LinkNotFoundException();
        }
    }

    @Override
    public List<Link> getListLinkToCheck(Duration after, int limit) {
        return linkRepository.findLinksCheckedAfter(after, limit);
    }

    @Override
    public List<Long> getListOfChatId(Long linkId) {
        return chatLinkRepository.findAllByLinkId(linkId);
    }

    @Override
    public void update(Long linkId, OffsetDateTime lastModified) {
        if (linkRepository.findById(linkId).isEmpty()) {
            throw new LinkNotFoundException();
        }
        linkRepository.update(linkId, lastModified);
    }

    @Override
    public void checkNow(Long linkId) {
        linkRepository.checkNow(linkId);
    }
}
