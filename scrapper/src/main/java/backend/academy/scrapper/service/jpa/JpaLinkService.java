package backend.academy.scrapper.service.jpa;

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
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.entity.ChatLinkEntity;
import backend.academy.scrapper.repository.jpa.entity.FilterEntity;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.TagEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaChatLinkRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaChatRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaFilterRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaLinkRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaTagRepository;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class JpaLinkService implements LinkService {

    private final ClientFactory clientFactory;
    private final JpaChatLinkRepository chatLinkRepository;
    private final JpaChatRepository chatRepository;
    private final JpaLinkRepository linkRepository;
    private final JpaTagRepository tagRepository;
    private final JpaFilterRepository filterRepository;

    @Override
    public ListLinksResponse getListLinks(Long chatId) {
        List<ChatLinkEntity> chatLinks = chatLinkRepository.findByChatId(chatId);

        List<LinkResponse> linkResponses = chatLinks.stream()
                .map(chatLink -> {
                    LinkEntity link = chatLink.link();
                    return new LinkResponse(
                            link.id(),
                            URI.create(link.url()),
                            tagRepository.findTagNamesByChatLink(chatId, link.id()),
                            filterRepository.findFilterNamesByChatLink(chatId, link.id()));
                })
                .toList();

        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    @Override
    public LinkResponse addLink(AddLinkRequest request, Long chatId) {
        ChatEntity chat = chatRepository.findById(chatId).orElseThrow(() -> {
            log.info("user [{}] not save link: chat not found", chatId);
            return new ChatNotFoundException();
        });

        URI url = request.link();
        if (chatLinkRepository.existsByChatIdAndLinkUrl(chatId, url.toString())) {
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

        LinkEntity link = new LinkEntity();
        link.url(url.toString());
        link.description(linkInformation.title());
        link.updatedAt(linkInformation.events().getFirst().lastModified());
        link.lastCheckedAt(OffsetDateTime.now(ZoneId.systemDefault()));
        link = linkRepository.save(link);

        ChatLinkEntity chatLink = new ChatLinkEntity();
        chatLink.chat(chat);
        chatLink.link(link);
        chatLink = chatLinkRepository.save(chatLink);

        if (request.tags() != null) {
            Set<TagEntity> tags = request.tags().stream()
                    .map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> {
                        TagEntity newTag = new TagEntity();
                        newTag.name(tagName);
                        return tagRepository.save(newTag);
                    }))
                    .collect(Collectors.toSet());
            chatLink.tags(tags);
        }

        if (request.filters() != null) {
            Set<FilterEntity> filters = request.filters().stream()
                    .map(filterName -> filterRepository.findByName(filterName).orElseGet(() -> {
                        FilterEntity newFilter = new FilterEntity();
                        newFilter.name(filterName);
                        return filterRepository.save(newFilter);
                    }))
                    .collect(Collectors.toSet());
            chatLink.filters(filters);
        }

        chatLinkRepository.save(chatLink);

        return new LinkResponse(link.id(), url, request.tags(), request.filters());
    }

    @Override
    public LinkResponse removeLink(URI url, Long tgChatId) {
        LinkEntity link = linkRepository.findByUrl(url.toString()).orElseThrow(LinkNotFoundException::new);

        ChatLinkEntity chatLink =
                chatLinkRepository.findByChatIdAndLinkId(tgChatId, link.id()).orElseThrow(LinkNotFoundException::new);

        List<String> tags = chatLink.tags().stream().map(TagEntity::name).collect(Collectors.toList());

        List<String> filters =
                chatLink.filters().stream().map(FilterEntity::name).collect(Collectors.toList());

        chatLinkRepository.delete(chatLink);
        if (chatLinkRepository.countByLinkId(link.id()) == 0) {
            linkRepository.delete(link);
        }

        tags.forEach(tagName -> {
            if (chatLinkRepository.countByTagName(tagName) == 0) {
                tagRepository.deleteByName(tagName);
            }
        });

        filters.forEach(filterName -> {
            if (chatLinkRepository.countByFilterName(filterName) == 0) {
                filterRepository.deleteByName(filterName);
            }
        });

        return new LinkResponse(link.id(), url, tags, filters);
    }

    @Override
    public List<Link> getListLinkToCheck(Duration after, int limit) {
        return linkRepository
                .findAllByLastCheckedAtBefore(
                        OffsetDateTime.now(ZoneId.systemDefault()).minus(after), Limit.of(limit))
                .stream()
                .map(LinkEntity::toDto)
                .toList();
    }

    @Override
    public List<Long> getListOfChatId(Long linkId, String userFilter) {
        if (userFilter == null || userFilter.isEmpty()) {
            var link = linkRepository.findById(linkId).orElseThrow();
            return link.chats().stream().map(ChatEntity::id).toList();
        }
        return chatLinkRepository.findAllByLinkIdWithFilter(linkId, "user=" + userFilter);
    }

    @Override
    public void update(Long linkId, OffsetDateTime lastModified) {
        var link = linkRepository.findById(linkId).orElseThrow();
        link.updatedAt(lastModified);
    }

    @Override
    public void checkNow(Long linkId) {
        var link = linkRepository.findById(linkId).orElseThrow();
        link.lastCheckedAt(OffsetDateTime.now(ZoneId.systemDefault()));
    }
}
