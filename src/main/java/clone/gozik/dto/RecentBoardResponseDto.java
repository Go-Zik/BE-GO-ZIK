package clone.gozik.dto;

import clone.gozik.entity.Board;
import lombok.Getter;

@Getter
public class RecentBoardResponseDto {
    private Long id;
    private String logo;
    private String nickname;
    private Integer viewcount;

    public RecentBoardResponseDto(Board board){
        id=board.getId();
        logo=board.getLogo();
        nickname = board.getNickname();
        viewcount = board.getViewCount();
    }
}
