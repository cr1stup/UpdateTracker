package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.Link;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultLinkRepository implements LinkRepository {

    private static final Map<Link, Set<Long>> linkChats = new HashMap<>();

    @Override
    public Long addChatByLink(Long id, Link link) {
        linkChats.computeIfAbsent(link, _ -> new HashSet<>()).add(id);
        return (long) linkChats.size() + 1;
    }

    @Override
    public Link removeChatByLink(Long id, URI url) {
        for (var link : linkChats.keySet()) {
            if (link.url().equals(url.toString())) {
                linkChats.get(link).remove(id);
                if (linkChats.get(link).isEmpty()) {
                    linkChats.remove(link);
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
