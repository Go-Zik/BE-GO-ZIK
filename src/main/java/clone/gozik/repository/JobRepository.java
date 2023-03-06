package clone.gozik.repository;

import clone.gozik.entity.Board;
import clone.gozik.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByBoard(Board board);
    void deleteByBoard(Board board);
}
