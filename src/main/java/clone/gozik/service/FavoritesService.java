package clone.gozik.service;


import clone.gozik.dto.AllBoardResponseDto;
import clone.gozik.dto.AllJobResponseDto;
import clone.gozik.dto.MessageDto;
import clone.gozik.entity.*;
import clone.gozik.exception.CustomException;
import clone.gozik.repository.BoardRepository;
import clone.gozik.repository.FavoritesRepository;
import clone.gozik.repository.JobRepository;
import clone.gozik.repository.MemberRepository;
import clone.gozik.security.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    private final FavoritesRepository favoritesRepository;
    private  final JobRepository jobRepository;


    @Transactional
    public MessageDto doOrCancle(MemberDetailsImpl memberDetails, Long boardid) {

        Member member = memberRepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(
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

    @Transactional
    public int favorites(Long boardid) {

        List<Favorites> favorites = favoritesRepository.findAllByBoardId(boardid);

        int count = favorites.size();

        return count;
    }

    @Transactional
    public HashMap<Long,Long> allfavor() {

        List<Favorites> allfavors = favoritesRepository.findAll();

        HashMap<Long, Long> boardAndCount = new HashMap<>();

        for (Favorites i : allfavors) {
            System.out.println("i.getBoard().getId() = " + i.getBoard().getId());
            if (boardAndCount.containsKey(i.getBoard().getId())){
                System.out.println("boardAndCount = " + boardAndCount.get(i.getBoard().getId()));
                boardAndCount.put(i.getBoard().getId(),boardAndCount.get(i.getBoard().getId())+1);
            }else {
                boardAndCount.put(i.getBoard().getId(),1L);
            }
        }

        return boardAndCount;
    }
    @Transactional
    public List getmembersfavor(MemberDetailsImpl memberDetails) {

        List<Favorites> membersfavors = favoritesRepository.findAllByMember_Id(memberDetails.getMember().getId());

        List<AllBoardResponseDto> memberfavorboards = new ArrayList<>();
        for (Favorites i : membersfavors) {
            List<Job> jobList = jobRepository.findByBoard(i.getBoard());
            List<AllJobResponseDto> jobResponse = new ArrayList<>();
            for (Job job : jobList) {
                jobResponse.add(new AllJobResponseDto(job));
            }
            memberfavorboards.add(new AllBoardResponseDto(i.getBoard(),jobResponse));

        }
        return memberfavorboards;

    }
}
