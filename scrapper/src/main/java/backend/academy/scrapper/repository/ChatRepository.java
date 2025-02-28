package backend.academy.scrapper.repository;

import java.util.HashSet;
import java.util.Set;

public class ChatRepository {
    private static final Set<Long> chats = new HashSet<>();

    public static void add(Long id) {
        chats.add(id);
    }

    public static void delete(Long id) {
        chats.remove(id);
    }
}
