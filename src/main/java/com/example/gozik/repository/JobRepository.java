package clone.gozic.repository;

import clone.gozic.entity.Board;
import clone.gozic.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByBoard(Board board);
}
