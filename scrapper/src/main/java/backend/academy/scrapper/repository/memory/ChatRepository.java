package backend.academy.scrapper.repository.memory;

import backend.academy.scrapper.dto.Link;
import java.net.URI;
import java.util.Set;

public interface ChatRepository {
    void addChat(Long id);

    void deleteChat(Long id);

    void addLinkByChat(Long id, Link link);

    void deleteLinkByChat(Long id, URI link);

    Set<Link> getLinksByChat(Long id);

    boolean isChatExist(Long id);

    boolean isLinkByChatExist(Long id, URI link);
}
