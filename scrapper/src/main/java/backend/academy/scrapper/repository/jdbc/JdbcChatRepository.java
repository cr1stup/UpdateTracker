package backend.academy.scrapper.repository.jdbc;

public interface JdbcChatRepository {

    void add(long chatId);

    void remove(long chatId);

    boolean isExists(long chatId);

}
