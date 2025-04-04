package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatLinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaChatLinkRepository extends JpaRepository<ChatLinkEntity, Long> {
    List<ChatLinkEntity> findByChatId(Long chatId);

    Optional<ChatLinkEntity> findByChatIdAndLinkId(Long chatId, Long linkId);

    boolean existsByChatIdAndLinkUrl(Long chatId, String url);

    @Query("SELECT COUNT(cl) FROM ChatLinkEntity cl WHERE cl.link.id = :linkId")
    long countByLinkId(@Param("linkId") Long linkId);

    @Query("SELECT COUNT(cl) FROM ChatLinkEntity cl JOIN cl.tags t WHERE t.name = :tagName")
    long countByTagName(@Param("tagName") String tagName);

    @Query("SELECT COUNT(cl) FROM ChatLinkEntity cl JOIN cl.filters f WHERE f.name = :filterName")
    long countByFilterName(@Param("filterName") String filterName);
}
