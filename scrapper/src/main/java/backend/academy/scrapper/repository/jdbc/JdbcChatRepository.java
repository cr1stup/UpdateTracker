package backend.academy.scrapper.repository.jdbc;

import java.util.List;

public interface JdbcChatRepository {

    List<Long> findAll();

    void add(long chatId);

    void remove(long chatId);

    boolean isExists(long chatId);
}
