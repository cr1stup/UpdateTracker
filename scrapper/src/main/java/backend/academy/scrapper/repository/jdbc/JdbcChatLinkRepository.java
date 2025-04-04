package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.dto.Link;
import java.net.URI;
import java.util.List;

public interface JdbcChatLinkRepository {

    List<Link> findAllByChatId(long chatId);

    List<Long> findAllByLinkId(long linkId);

    Long add(long chatId, long linkId);

    boolean isLinkAlreadyExist(long chatId, URI url);

    void remove(long chatId, long linkId);

}
