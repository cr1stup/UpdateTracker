package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.Link;
import java.net.URI;
import java.util.Set;

public interface LinkRepository {
    Long addChatByLink(Long id, Link link);

    Link removeChatByLink(Long id, URI url);

    Set<Link> getAllLinks();

    Set<Long> getAllChatIdByLink(Link link);
}
