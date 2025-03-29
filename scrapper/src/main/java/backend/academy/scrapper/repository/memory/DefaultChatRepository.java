package backend.academy.scrapper.repository.memory;

import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.exception.LinkNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultChatRepository implements ChatRepository {

    private static final Map<Long, Set<Link>> chatLinks = new HashMap<>();

    @Override
    public void addChat(Long id) {
        chatLinks.put(id, new HashSet<>());
    }

    @Override
    public void deleteChat(Long id) {
        chatLinks.remove(id);
    }

    @Override
    public void addLinkByChat(Long id, Link link) {
        chatLinks.get(id).add(link);
    }

    @Override
    public void deleteLinkByChat(Long id, URI url) {
        if (!isLinkByChatExist(id, url)) {
            throw new LinkNotFoundException(url);
        }
        chatLinks.get(id).removeIf(link -> link.url().equals(url.toString()));
    }

    @Override
    public Set<Link> getLinksByChat(Long id) {
        return chatLinks.get(id);
    }

    @Override
    public boolean isChatExist(Long id) {
        return chatLinks.containsKey(id);
    }

    @Override
    public boolean isLinkByChatExist(Long id, URI url) {
        for (var link : chatLinks.get(id)) {
            if (link.url().equals(url.toString())) {
                return true;
            }
        }
        return false;
    }
}
