package clone.gozik.dto;

import clone.gozik.entity.Board;
import clone.gozik.entity.Job;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MainBoardResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private String logo;
    private LocalDate lastdate;
    private Integer viewconut;
    private String jobdetail;

    public MainBoardResponseDto(Board board, Job job){
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = board.getNickname();
        this.logo= board.getLogo();
        this.lastdate=board.getLastDate();
        this.viewconut=board.getViewCount();
        this.jobdetail= job.getJobDetail();
    }
    public MainBoardResponseDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = board.getNickname();
        this.logo= board.getLogo();
        this.lastdate=board.getLastDate();
        this.viewconut=board.getViewCount();
        this.jobdetail= "백엔드 개발자";
    }
}
