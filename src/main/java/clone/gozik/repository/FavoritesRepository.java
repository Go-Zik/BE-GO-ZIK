package clone.gozik.repository;

import clone.gozik.entity.Board;
import clone.gozik.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    Optional<Favorites> findByBoardIdAndMember_Id(Long boardid, Long memberid);

    Optional<Favorites> deleteFavoritesByBoard_IdAndMember_Id(Long boardid, Long memberid);

    List<Favorites> findAllByBoardId(Long boardid);
    Integer countByBoard(Board board);

    List<Favorites> findAllByMember_Id(Long memberid);


}
