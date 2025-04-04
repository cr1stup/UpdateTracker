package backend.academy.scrapper.repository.jdbc;

import java.util.List;

public interface JdbcTagRepository {
    void add(Long chatLinkId, List<String> tags);

    List<String> findAllByChatLink(Long chatId, Long linkId);

    void remove(String tag);

    List<Long> findByName(String tag);
}
