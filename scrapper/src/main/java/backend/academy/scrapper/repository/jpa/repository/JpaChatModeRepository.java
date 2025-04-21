package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatModeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaChatModeRepository extends JpaRepository<ChatModeEntity, Long> {

    Optional<ChatModeEntity> findByChatId(Long chatId);

    @Query(
            """
        SELECT cm.chatId FROM ChatModeEntity cm
        JOIN cm.mode m
        WHERE m.name = :modeName
    """)
    List<Long> findChatIdsByModeName(@Param("modeName") String modeName);
}
