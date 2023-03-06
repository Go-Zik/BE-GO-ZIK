package clone.gozik.service;


import clone.gozik.dto.MessageDto;
import clone.gozik.entity.*;
import clone.gozik.exception.CustomException;
import clone.gozik.repository.BoardRepository;
import clone.gozik.repository.FavoritesRepository;
import clone.gozik.repository.MemberRepository;
import clone.gozik.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    MemberRepository memberRepository;

    BoardRepository boardRepository;

    FavoritesRepository favoritesRepository;


    @Transactional
    public MessageDto doOrCancle(UserDetailsImpl userDetails, Long boardid) {

        Member member = memberRepository.findByEmail(userDetails.getUser().getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_AUTHOR)
        );

        Board board = boardRepository.findById(boardid).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_AUTHOR)
        );

        if (favoritesRepository.findByBoardIdAndMember_Id(boardid,member.getId()).isPresent()){
            favoritesRepository.deleteFavoritesByBoard_IdAndMember_Id(boardid, member.getId());
            return MessageDto.of(SuccessCode.NOT_LIKE_SUCCESS);
        }else {
            favoritesRepository.saveAndFlush(new Favorites(member,board));
            return MessageDto.of(SuccessCode.LIKE_SUCCESS);
        }
    }


    public int favorites(Long boardid) {

        List<Favorites> favorites = favoritesRepository.findAllByBoardId(boardid);

        int count = favorites.size();

        return count;
    }







    public HashMap<Long,Long> allfavor() {

        List<Favorites> allfavors = favoritesRepository.findAll();

        HashMap<Long, Long> boardAndCount = new HashMap<>();

        for (Favorites i : allfavors) {
            if (boardAndCount.containsKey(i.getBoard().getId())){
                boardAndCount.put(i.getBoard().getId(),boardAndCount.get(i.getBoard().getId()+1));
            }else {
                boardAndCount.put(i.getBoard().getId(),1L);
            }
        }

        return boardAndCount;
    }

    public List<Board> getmembersfavor(UserDetailsImpl userDetails) {

        List<Favorites> membersfavors = favoritesRepository.findAllByMember_Id(userDetails.getUser().getId());

        List<Board> memberfavorboards = new ArrayList<>();
        for (Favorites i : membersfavors) {
            memberfavorboards.add(i.getBoard());
        }
        return memberfavorboards;

    }
}
