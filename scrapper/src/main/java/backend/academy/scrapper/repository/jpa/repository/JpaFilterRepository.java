package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.FilterEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaFilterRepository extends JpaRepository<FilterEntity, Long> {
    Optional<FilterEntity> findByName(String name);

    @Query("SELECT f.name FROM FilterEntity f JOIN f.chatLinks cl WHERE cl.chat.id = :chatId AND cl.link.id = :linkId")
    List<String> findFilterNamesByChatLink(@Param("chatId") Long chatId, @Param("linkId") Long linkId);

    @Modifying
    @Query("DELETE FROM FilterEntity f WHERE f.name = :name")
    void deleteByName(@Param("name") String name);
}
