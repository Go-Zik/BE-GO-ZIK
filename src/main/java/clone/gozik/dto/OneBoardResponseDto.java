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
    private LocalDate startDate;
    private LocalDate lastDate;
    private List job;
    private String image;
    private String logo;
    private Integer viewcount;
    private Integer favorite;

    public OneBoardResponseDto(Board board, List job,Integer favorite) {
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.nickname = board.getNickname();
        this.startDate = board.getStartDate();
        this.lastDate = board.getLastDate();
        this.job = job;
        this.image = board.getImage();
        this.logo = board.getLogo();
        this.viewcount= board.getViewCount();
        this.favorite=favorite;
    }
}
