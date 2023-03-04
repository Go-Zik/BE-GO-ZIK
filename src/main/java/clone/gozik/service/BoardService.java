package clone.gozik.service;

import clone.gozik.dto.*;
import clone.gozik.entity.Board;
import clone.gozik.entity.Job;
import clone.gozik.entity.Member;
import clone.gozik.entity.RecruitTypeEnum;
import clone.gozik.repository.BoardRepository;
import clone.gozik.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private  final JobRepository jobRepository;

    //private final MemberRepository memberrepository;



    @Transactional(readOnly = true)
    public List allBoard() {
        List<Board> boardList= boardRepository.findAll();
        List<AllBoardResponseDto> boardResponse = new ArrayList<>();
        for (Board board : boardList) {
            List<Job> jobList = jobRepository.findByBoard(board);
            List<AllJobResponseDto> jobResponse = new ArrayList<>();
            for (Job job : jobList) {
                jobResponse.add(new AllJobResponseDto(job));
            }
            boardResponse.add(new AllBoardResponseDto(board,jobResponse));
        }
        return boardResponse;
    }

    @Transactional(readOnly = true)
    public OneBoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("글이 존재하지 않습니다"));
        List<Job> jobList = jobRepository.findByBoard(board);
        List<OneJobResponseDto> jobResponse = new ArrayList<>();
        for (Job job : jobList) {
            jobResponse.add(new OneJobResponseDto(job));
        }
        OneBoardResponseDto boardResponse = new OneBoardResponseDto(board,jobResponse);
        return boardResponse;
    }

    @Transactional
    public void createBoard(RequestBoardDto requestBoardDto, MultipartFile image, MultipartFile logo) {
        //임시 코드 1 > 구현시 모두 제거
        String nickname = "임시닉네임 로그인구현시 변경";
        Member member = new Member();//임시멤버, 멤버 리포지토리 구현시 제거
        String imageurl = "임시이미지 url";
        String logourl = "로고 url";
        //임시코드1은 여기까지
        LocalDate startDate = extractDate(requestBoardDto.getStartDate());//String에서 날짜추출
        if(requestBoardDto.isRecruitmentPeriod()){
            Board board = new Board(requestBoardDto,nickname,startDate,member,imageurl,logourl);
            boardRepository.save(board);
        }else{
            LocalDate lastDate = extractDate(requestBoardDto.getEndDate());
            Board board = new Board(requestBoardDto,nickname,lastDate,startDate,member,imageurl,logourl);
            boardRepository.save(board);
        }
    }

    @Transactional
    public void updateBoard(Long id, RequestBoardDto boardRequestDto, MultipartFile image, MultipartFile logo) {
        Board board =  boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("글이 존재하지 않습니다"));
        //임시 코드 2 > 구현시 모두 제거
        String nickname = "임시닉네임 로그인구현시 변경";
        Member member = new Member();//임시멤버, 멤버 리포지토리 구현시 제거
        String imageurl = "임시이미지 url";
        String logourl = "로고 url";
        //임시코드2는 여기까지
        LocalDate startDate = extractDate(boardRequestDto.getStartDate());//String에서 날짜추출
        if(boardRequestDto.isRecruitmentPeriod()){
            board.update(boardRequestDto,startDate,member,imageurl,logourl);
            boardRepository.save(board);
        }else{
            LocalDate lastDate = extractDate(boardRequestDto.getEndDate());
             board.update(boardRequestDto,lastDate,startDate,member,imageurl,logourl);
            boardRepository.save(board);
        }
    }

    @Transactional
    public void doneBoard(Long id) {
        Board board= boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("글이 존재하지 않습니다"));
        if(board.getRecruitmentperiod()== RecruitTypeEnum.ONGOING)
            board.closed();
        else{
            throw new IllegalArgumentException("수시채용이 아닙니다");
        }
    }

    private LocalDate extractDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
