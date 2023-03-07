package clone.gozik.repository;

import clone.gozik.entity.Board;
import clone.gozik.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {
    Page<Board> findAll(Pageable pageable);
    Page<Board> findAllByOrderByIdDesc(Pageable pageable);
    Optional<Board> findByIdAndMember(Long id, Member member);
    void deleteById(Long id);
}
