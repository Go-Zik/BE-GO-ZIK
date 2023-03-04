package clone.gozik.entity;

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

    @Enumerated(value = EnumType.STRING)
    private EmployeeType incruittype;

    @Column
    private String jobDetail;

    public Job(EmployeeType incruittype, Board board) {
        this.incruittype = incruittype;
        this.board = board;
    }
}
