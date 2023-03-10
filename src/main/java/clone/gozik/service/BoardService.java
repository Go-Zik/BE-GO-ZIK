package clone.gozik.service;

import clone.gozik.S3.S3Uploader;
import clone.gozik.dto.*;
import clone.gozik.entity.*;
import clone.gozik.exception.CustomException;
import clone.gozik.repository.*;
import clone.gozik.security.MemberDetailsImpl;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

import static clone.gozik.entity.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final JobRepository jobRepository;

    private final MemberRepository memberrepository;

    private final LogoAndImageRepository logoAndImageRepository;

    private final FavoritesRepository favoritesRepository;

    @Autowired
    private final S3Uploader s3Uploader;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;


    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;


    @Transactional(readOnly = true)
    public List mainBoard() {
       Pageable mainpage = PageRequest.of(0,24);
       Page<Board> boardlist = boardRepository.findAllByOrderByIdDesc(mainpage);
       List<MainBoardResponseDto> boardResponse = new ArrayList<>();
        for (Board board : boardlist) {
            try{
            List<Job> job = jobRepository.findByBoard(board);
            boardResponse.add(new MainBoardResponseDto(board,job.get(0)));}
            catch (IndexOutOfBoundsException e){
                boardResponse.add(new MainBoardResponseDto(board));
            }
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

    @Transactional
    public OneBoardResponseDto getBoard(Long id,MemberDetailsImpl memberDetails) {
        Member member = null;
        Boolean hasfav = false;
        if(memberDetails!=null){
            member=memberrepository.findByEmail(memberDetails.getEmail()).orElseThrow(()->new CustomException(UNREGISTER_EMAIL));
            Optional<Favorites> fav =  favoritesRepository.findByBoardIdAndMember_Id(id,member.getId());
            if(fav.isEmpty())
            {
                hasfav=false;
            }else if(fav.isPresent()){
                hasfav=true;
            }
        }
        Board board = boardRepository.findAndLockById(id);//.orElseThrow(()->new IllegalArgumentException("?????? ???????????? ????????????"));
        boardRepository.viewBoard(id);
        List<Job> jobList = jobRepository.findByBoard(board);
        List<OneJobResponseDto> jobResponse = new ArrayList<>();
        for (Job job : jobList) {
            jobResponse.add(new OneJobResponseDto(job));
        }
        Integer fav =favoritesRepository.countByBoard(board);
        OneBoardResponseDto boardResponse = new OneBoardResponseDto(board,jobResponse,fav,hasfav);
        return boardResponse;
    }

    @Transactional
    public ResponseEntity<MessageDto> createBoard(MemberDetailsImpl memberDetails, RequestBoardDto requestBoardDto, MultipartFile image, MultipartFile logo) {
        Member member = memberrepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(()->new CustomException(UNREGISTER_EMAIL));
        String nickname = member.getNickName();
        Board board = null;
        String imageurl = "";
        String logourl = "";

        List<String> logodata = new ArrayList<>();
        List<String> imagedata = new ArrayList<>();
        if (logo.equals(null)){ //?????? ????????? ??????
            throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
        }else {                 //?????? ?????? ??????
            logodata = s3Uploader.upload(logo,"logo");
            logourl = logodata.get(1);
        }

        if (logo.equals(null)){ //????????? ????????? ??????
            throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
        }else {                 //????????? ?????? ??????
            imagedata = s3Uploader.upload(image,"image");
            imageurl = imagedata.get(1);
        }

        LocalDate startDate = extractDate(requestBoardDto.getStartdate());//String?????? ????????????
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
    public ResponseEntity<MessageDto> updateBoard(Long id, RequestBoardDto boardRequestDto, MultipartFile image, MultipartFile logo, MemberDetailsImpl memberDetails) {
        Board board =  boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("?????? ???????????? ????????????"));
        Member member = memberrepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(()->new CustomException(UNREGISTER_EMAIL));
        if(!board.getNickname().equals(member.getNickName()))
        {
            throw new CustomException(NOT_AUTHOR);
        }
        String imageurl = "";
        String logourl = "";

        jobRepository.deleteByBoard(board);//?????? ????????? ????????? job??? ????????? ?????? ???????????? ??????
        List<RequestJobDto> jobs = boardRequestDto.getJob();
        for (RequestJobDto jobDto : jobs) {
            jobRepository.save(new Job(jobDto,board));
        }
        LogoAndImage logoAndImage = logoAndImageRepository.findByBoardId(id)
                .orElseThrow(()->new CustomException(ErrorCode.NULL_IMAGE_DATA));

        List<String> logodata = new ArrayList<>();
        List<String> imagedata = new ArrayList<>();
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region).build();

        if (logo.isEmpty()){ //????????? ????????? ?????? ??????
            logourl = "";
            String deletelogokey = logoAndImage.getLogoKey();
            try{
                s3.deleteObject(bucket,deletelogokey);
            }catch (Exception e){
                throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
            }
            logoAndImage.setLogoKey("");
            logoAndImage.setLogoUrl("");
        }else {                 //????????? ???????????? ?????? ????????? ?????? ?????? ?????? ??????
            //?????? logo ?????? ??????
            String deletelogokey = logoAndImage.getLogoKey();
            System.out.println(deletelogokey);

            try{
                s3.deleteObject(bucket,deletelogokey);
            }catch (Exception e){
                throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
            }
            //??? ?????? ?????? ??????
            logodata = s3Uploader.upload(logo,"logo");
            logoAndImage.updatelogo(logodata);
            logourl = logodata.get(1);
        }


        if (image.isEmpty()){ //???????????? ????????? ?????? ??????
            imageurl = "";
            String deletelogokey = logoAndImage.getImageKey();
            try{
                s3.deleteObject(bucket,deletelogokey);
            }catch (Exception e){
                throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
            }
            logoAndImage.setImageKey("");
            logoAndImage.setImageUrl("");
        }else {                 //???????????? ???????????? ?????? ????????? ?????? ?????? ?????? ??????
            //?????? image ?????? ??????
            String deleteimagekey = logoAndImage.getImageKey();
            try{
                s3.deleteObject(bucket,deleteimagekey);
            }catch (Exception e){
                e.printStackTrace();
            }
            //??? ????????? ?????? ??????
            imagedata = s3Uploader.upload(image,"image");
            logoAndImage.updateimage(imagedata);
            imageurl = imagedata.get(1);
        }

        LocalDate startDate = extractDate(boardRequestDto.getStartdate());//String?????? ????????????
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
    public ResponseEntity<MessageDto> doneBoard(Long id, MemberDetailsImpl memberDetails) {
        Member member = memberrepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(()->new CustomException(UNREGISTER_EMAIL));
        Board board= boardRepository.findByIdAndMember(id,member).orElseThrow(()->new CustomException(ErrorCode.NULL_BOARD_DATA));
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


    public ResponseEntity<MessageDto> delete(Long id, MemberDetailsImpl memberDetails) {
        Member member = memberrepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(()->new CustomException(UNREGISTER_EMAIL));
        boardRepository.findByIdAndMember(id,member).orElseThrow(()->new CustomException(NULL_BOARD_DATA));

        //S3??? ???????????? ?????? ????????? ??????
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region).build();
        LogoAndImage logoAndImage = logoAndImageRepository.findByBoardId(id)
                .orElseThrow(()->new CustomException(ErrorCode.NULL_IMAGE_DATA));

        System.out.println("???????????? ??????");
        String deletelogokey = logoAndImage.getLogoKey();
        if (!deletelogokey.equals("")){
            try{
                s3.deleteObject(bucket,deletelogokey);
                System.out.println("??????????????????");
            }catch (Exception e){
                throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
            }
        }
        String deleteimagekey = logoAndImage.getImageKey();
        if (!deleteimagekey.equals("")){
            try{
                s3.deleteObject(bucket,deleteimagekey);
                System.out.println("??????????????? ??????");
            }catch (Exception e){
                throw new CustomException(ErrorCode.NULL_IMAGE_DATA);
            }
        }

        boardRepository.deleteById(id);



        return ResponseEntity.ok()
                .body(MessageDto.of(SuccessCode.BLOG_DELETE_SUCCESS));
    }

    public List recent() {
        Pageable recentpage = PageRequest.of(0,6);
        Page<Board> boardlist = boardRepository.findAllByOrderByIdDesc(recentpage);
        List<RecentBoardResponseDto> boardResponse = new ArrayList<>();
        for (Board board : boardlist) {
            boardResponse.add(new RecentBoardResponseDto(board));
        }
        return boardResponse;
    }
}
