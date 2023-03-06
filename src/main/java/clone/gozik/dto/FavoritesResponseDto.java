package clone.gozik.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoritesResponseDto {

    private long boardid;

    private long favorcount;

    public FavoritesResponseDto(long boardid, long favorcount) {
        this.boardid = boardid;
        this.favorcount = favorcount;
    }
}
