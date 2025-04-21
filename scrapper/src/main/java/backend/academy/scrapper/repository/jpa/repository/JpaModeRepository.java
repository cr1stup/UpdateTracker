package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ModeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaModeRepository extends JpaRepository<ModeEntity, Long> {
    Optional<ModeEntity> findByName(String name);
}
