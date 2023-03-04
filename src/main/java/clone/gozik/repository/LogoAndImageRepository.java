package clone.gozik.repository;

import clone.gozik.entity.LogoAndImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogoAndImageRepository extends JpaRepository<LogoAndImage, Long> {

    Optional<LogoAndImage> findByBoardId(Long id);

}
