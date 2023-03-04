package clone.gozik.dto;

import clone.gozik.entity.Board;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MainBoardResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private String logo;
    private LocalDate lastdate;
    private Integer viewconut;

    public MainBoardResponseDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = board.getNickname();
        this.logo= board.getLogo();
        this.lastdate=board.getLastDate();
        this.viewconut=board.getViewCount();
    }
}
