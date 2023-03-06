package clone.gozik.service;

import clone.gozik.S3.S3Uploader;
import clone.gozik.dto.*;
import clone.gozik.entity.*;
import clone.gozik.exception.CustomException;
import clone.gozik.repository.BoardRepository;
import clone.gozik.repository.JobRepository;
import clone.gozik.repository.MemberRepository;
import clone.gozik.repository.LogoAndImageRepository;
import clone.gozik.security.UserDetailsImpl;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static clone.gozik.entity.ErrorCode.NULL_BOARD_DATA;
import static clone.gozik.entity.ErrorCode.UNREGISTER_EMAIL;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private  final JobRepository jobRepository;

    private final MemberRepository memberrepository;

    private final LogoAndImageRepository logoAndImageRepository;

    private final S3Uploader s3Uploader;

    @Value("gozik")
    private String bucket;

    @Value("ap-northeast-2")
    private String region;

    @Transactional(readOnly = true)
    public List mainBoard() {
       Pageable mainpage = PageRequest.of(1,27);
       Page<Board> boardList = boardRepository.findAll(mainpage);
       List<MainBoardResponseDto> boardResponse = new ArrayList<>();
        for (Board board : boardList) {
            boardResponse.add(new MainBoardResponseDto(board));
        }
        return boardResponse;
    }


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
    public ResponseEntity<MessageDto> createBoard(RequestBoardDto requestBoardDto, MultipartFile image, MultipartFile logo) {
        //임시 코드 1 > 구현시 모두 제거
        String nickname = "임시닉네임 로그인구현시 변경";
        Member member = new Member();//임시멤버, 멤버 리포지토리 구현시 제거
        //임시코드1은 여기까지
        //파일 저장하며 url 받아오기
        Board board = null;
        String imageurl = "";
        String logourl = "";

        List<String> logodata = new ArrayList<>();
        List<String> imagedata = new ArrayList<>();
        if (logo.equals(null)){ //로고 안넣은 경우
            throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
        }else {                 //로고 넣은 경우
            logodata = s3Uploader.upload(logo,"logo");
            logourl = logodata.get(1);
        }

        if (logo.equals(null)){ //이미지 안넣은 경우
            throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
        }else {                 //이미지 넣은 경우
            imagedata = s3Uploader.upload(image,"image");
            imageurl = imagedata.get(1);
        }

        LocalDate startDate = extractDate(requestBoardDto.getStartdate());//String에서 날짜추출
        if(requestBoardDto.isRecruitmentperiod()){
            board = new Board(requestBoardDto,nickname,startDate,member,imageurl,logourl);
        }else{
            LocalDate lastDate = extractDate(requestBoardDto.getEnddate());
            board = new Board(requestBoardDto,nickname,lastDate,startDate,member,imageurl,logourl);;
        }
        boardRepository.save(board);
        logoAndImageRepository.save(new LogoAndImage(logodata, imagedata, board));
        List<RequestJobDto> jobs = requestBoardDto.getJob();
        for (RequestJobDto jobDto : jobs) {
            jobRepository.save(new Job(jobDto,board));
        }
        return ResponseEntity.ok()
                .body(MessageDto.of(SuccessCode.BLOG_POST_SUCCESS));
    }

    @Transactional
    public ResponseEntity<MessageDto> updateBoard(Long id, RequestBoardDto boardRequestDto, MultipartFile image, MultipartFile logo) {
        Board board =  boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("글이 존재하지 않습니다"));
        //임시 코드 2 > 구현시 모두 제거
        String nickname = "임시닉네임 로그인구현시 변경";
        Member member = new Member();//임시멤버, 멤버 리포지토리 구현시 제거
        //임시코드2는 여기까지
        String imageurl = "";
        String logourl = "";

        jobRepository.deleteByBoard(board);//기존 보드와 연결된 job를 지우고 다시 작성하는 코드
        List<RequestJobDto> jobs = boardRequestDto.getJob();
        for (RequestJobDto jobDto : jobs) {
            jobRepository.save(new Job(jobDto,board));
        }
        LogoAndImage logoAndImage = logoAndImageRepository.findByBoardId(id)
                .orElseThrow(()->new CustomException(ErrorCode.NULL_IMAGE_DATA));



        List<String> logodata = new ArrayList<>();
        List<String> imagedata = new ArrayList<>();
        if (logo.equals(null)){ //로고를 없애고 싶은 경우
            logourl = "";
        }else {                 //로고를 바꾸거나 원래 있었던 걸로 하고 싶은 경우
            //기존 logo 파일 삭제
            String deletelogokey = logoAndImage.getLogoKey();
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
            try{
                s3.deleteObject(bucket,deletelogokey);
            }catch (Exception e){
                throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
            }
            //새 로고 파일 등록
            logodata = s3Uploader.upload(logo,"logo");
            logoAndImage.updatelogo(logodata);
            logourl = logodata.get(1);
        }

        if (image.equals(null)){ //이미지를 없애고 싶은 경우
            imageurl = "";
        }else {                 //이미지를 바꾸거나 원래 있었던 걸로 하고 싶은 경우
            //기존 image 파일 삭제
            String deleteimagekey = logoAndImage.getImageKey();
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
            try{
                s3.deleteObject(bucket,deleteimagekey);
            }catch (Exception e){
                e.printStackTrace();
            }
            //새 이미지 파일 등록
            imagedata = s3Uploader.upload(image,"image");
            logoAndImage.updateimage(imagedata);
            imageurl = imagedata.get(1);
        }

        LocalDate startDate = extractDate(boardRequestDto.getStartdate());//String에서 날짜추출
        if(boardRequestDto.isRecruitmentperiod()){
            board.update(boardRequestDto,startDate,member,imageurl,logourl);
            boardRepository.save(board);
        }else{
            LocalDate lastDate = extractDate(boardRequestDto.getEnddate());
             board.update(boardRequestDto,lastDate,startDate,member,imageurl,logourl);
            boardRepository.save(board);
        }
        return ResponseEntity.ok()
                .body(MessageDto.of(SuccessCode.BLOG_PUT_SUCCESS));
    }

    @Transactional
    public ResponseEntity<MessageDto> doneBoard(Long id) {
        Board board= boardRepository.findById(id).orElseThrow(
                ()->new CustomException(ErrorCode.NULL_BOARD_DATA));
        if(board.getRecruitmentperiod()== RecruitTypeEnum.ONGOING)
            board.closed();
        else{
            throw new CustomException(ErrorCode.NOT_RECRUIT_TYPE);
        }
        return ResponseEntity.ok()
                .body(MessageDto.of(SuccessCode.BLOG_END_SUCCESS));
    }

    private LocalDate extractDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }


    public ResponseEntity<MessageDto> delete(Long id, UserDetailsImpl userDetails) {
        Member member = memberrepository.findByEmail(userDetails.getEmail()).orElseThrow(()->new CustomException(UNREGISTER_EMAIL));
        boardRepository.findByIdAndMember(id,member).orElseThrow(()->new CustomException(NULL_BOARD_DATA));
        boardRepository.deleteById(id);
        return ResponseEntity.ok()
                .body(MessageDto.of(SuccessCode.BLOG_DELETE_SUCCESS));
    }
}
