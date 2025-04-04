package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.TagEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaTagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);

    @Query("SELECT t.name FROM TagEntity t JOIN t.chatLinks cl WHERE cl.chat.id = :chatId AND cl.link.id = :linkId")
    List<String> findTagNamesByChatLink(@Param("chatId") Long chatId, @Param("linkId") Long linkId);

    @Modifying
    @Query("DELETE FROM TagEntity t WHERE t.name = :name")
    void deleteByName(@Param("name") String name);
}
