package clone.gozik.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class LogoAndImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "logoAndImage_id")
    @Column
    private long id;

    @Column(length = 1000, nullable = true)
    private String logoKey;


    @Column(length = 1000, nullable = true)
    private String logoUrl;

    @Column(length = 1000, nullable = true)
    private String imageKey;


    @Column(length = 1000, nullable = true)
    private String imageUrl;


    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "board_id")
    private Board board;

    public LogoAndImage(List<String>logodata, List<String>imagedata, Board board) {
        this.logoKey = logodata.get(0);
        this.logoUrl = logodata.get(1);
        this.imageKey = imagedata.get(0);
        this.imageUrl = imagedata.get(1);
        this.board = board;
    }

    public void updatelogo(List<String>logodata){
        this.logoKey = logodata.get(0);
        this.logoUrl = logodata.get(1);
    }

    public void updateimage(List<String>imagedata){
        this.logoKey = imagedata.get(0);
        this.logoUrl = imagedata.get(1);
    }


}
