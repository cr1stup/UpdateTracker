package backend.academy.scrapper.repository.memory;

import backend.academy.scrapper.dto.Link;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultLinkRepository implements LinkRepository {

    private static final Map<Link, Set<Long>> linkChats = new HashMap<>();

    @Override
    public Long addChatByLink(Long id, Link link) {
        linkChats.computeIfAbsent(link, k -> new HashSet<>()).add(id);
        return (long) linkChats.size() + 1;
    }

    @Override
    public Link removeChatByLink(Long id, URI url) {
        Iterator<Map.Entry<Link, Set<Long>>> iterator = linkChats.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Link, Set<Long>> entry = iterator.next();
            Link link = entry.getKey();
            Set<Long> chatIds = entry.getValue();

            if (link.url().equals(url.toString())) {
                chatIds.remove(id);
                if (chatIds.isEmpty()) {
                    iterator.remove();
                }
                return link;
            }
        }
        return null;
    }

    @Override
    public Set<Link> getAllLinks() {
        return linkChats.keySet();
    }

    @Override
    public Set<Long> getAllChatIdByLink(Link link) {
        return linkChats.getOrDefault(link, new HashSet<>());
    }
}
