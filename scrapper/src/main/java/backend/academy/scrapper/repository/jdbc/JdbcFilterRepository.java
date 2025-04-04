package backend.academy.scrapper.repository.jdbc;

import java.util.List;

public interface JdbcFilterRepository {
    void add(Long chatLinkId, List<String> filters);

    List<String> findAllByChatLink(Long chatId, Long linkId);

    List<Long> findByName(String filter);

    void remove(String filter);
}
