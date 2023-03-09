package clone.gozik.dto;

import clone.gozik.entity.Board;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
@Getter
public class OneBoardResponseDto {
    private String title;
    private String description;
    private String nickname;
    private LocalDate startdate;
    private LocalDate lastdate;
    private List job;
    private String image;
    private String logo;
    private Integer viewcount;
    private Integer favorite;
    private Boolean hasfav;

    public OneBoardResponseDto(Board board, List job,Integer favorite,Boolean hasfav) {
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.nickname = board.getNickname();
        this.startdate = board.getStartDate();
        this.lastdate = board.getLastDate();
        this.job = job;
        this.image = board.getImage();
        this.logo = board.getLogo();
        this.viewcount= board.getViewCount();
        this.favorite=favorite;
        this.hasfav=hasfav;
    }
}
