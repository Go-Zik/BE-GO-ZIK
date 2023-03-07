package clone.gozik.entity;

import clone.gozik.dto.RequestJobDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String incruittype;

    @Column
    private String jobDetail;

    public Job(RequestJobDto requestJobDto, Board board) {
        this.incruittype = requestJobDto.getIncruittype();
        this.jobDetail = requestJobDto.getJobdetail();
        this.board = board;
    }
}
