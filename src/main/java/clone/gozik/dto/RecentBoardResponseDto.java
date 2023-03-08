package clone.gozik.dto;

import clone.gozik.entity.Board;
import lombok.Getter;

@Getter
public class RecentBoardResponseDto {
    private String logo;
    private String nickname;
    private Integer viewcount;

    public RecentBoardResponseDto(Board board){
        logo=board.getLogo();
        nickname = board.getNickname();
        viewcount = board.getViewCount();
    }
}
