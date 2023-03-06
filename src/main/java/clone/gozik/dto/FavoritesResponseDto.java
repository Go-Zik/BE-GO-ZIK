package clone.gozik.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoritesResponseDto {

    private long boardid;

    private long favorcount;

    public FavoritesResponseDto(long boardid, long favorcount) {
        this.boardid = boardid;
        this.favorcount = favorcount;
    }
}
