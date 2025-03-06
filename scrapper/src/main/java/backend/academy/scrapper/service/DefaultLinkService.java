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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
            throw new ChatNotFoundException();
        }

        URI url = request.link();
        if (chatRepository.isLinkByChatExist(tgChatId, url)) {
            throw new LinkAlreadyAddedException(url);
        }

        LinkClient client = clientFactory.getClient(url);
        if (client == null) {
            throw new LinkIsNotSupportedException(url);
        }

        LinkInformation linkInformation = client.fetchInformation(url);
        if (linkInformation == null) {
            throw new LinkIsNotSupportedException(url);
        }

        Link link = new Link(
            0,
            url.toString(),
            request.tags(),
            request.filters(),
            linkInformation.lastModified(),
            OffsetDateTime.now(ZoneId.systemDefault())
        );

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
}
