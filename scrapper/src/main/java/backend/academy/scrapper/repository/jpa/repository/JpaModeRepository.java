package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ModeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaModeRepository extends JpaRepository<ModeEntity, Long> {
    Optional<ModeEntity> findByName(String name);
}
