package clone.gozik.repository;

import clone.gozik.entity.Board;
import clone.gozik.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {

    Page<Board> findAllByOrderByIdDesc(Pageable pageable);
    Optional<Board> findByIdAndMember(Long id, Member member);
    void deleteById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Board b WHERE b.id = :id")
    Board findAndLockById(Long id);

    @Modifying
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void viewBoard(Long id);
    //동시성 문제 쿼리로 해결

}
