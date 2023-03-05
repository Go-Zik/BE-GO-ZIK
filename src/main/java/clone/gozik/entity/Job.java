package clone.gozik.entity;

import clone.gozik.dto.RequestJobDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static clone.gozik.entity.EmployeeType.*;

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

    public Job(RequestJobDto requestJobDto, Board board) {
        this.incruittype = switch (requestJobDto.getIncruittype()){
            case "신입" -> NEW;
            case "인턴" -> INTERN;
            case "경력" -> EXPERIENCED;
            case "CONTRACT" -> CONTRACT;
            default -> throw new IllegalArgumentException("채용 형태를 확인해주세요" );
        };
        this.board = board;
    }
}
