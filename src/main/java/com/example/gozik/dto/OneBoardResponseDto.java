package clone.gozic.dto;

import clone.gozic.entity.Board;

import java.time.LocalDate;
import java.util.List;

public class OneBoardResponseDto {
    private String title;
    private String description;
    private String nickname;
    private LocalDate startDate;
    private LocalDate lastDate;
    private List job;
    private String image;
    private String logo;

    public OneBoardResponseDto(Board board,List job) {
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.nickname = board.getNickname();
        this.startDate = board.getStartDate();
        this.lastDate = board.getLastDate();
        this.job = job;
        this.image = board.getImage();
        this.logo = board.getLogo();
    }
}
