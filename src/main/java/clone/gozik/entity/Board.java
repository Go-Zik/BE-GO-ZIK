package clone.gozik.entity;

import clone.gozik.dto.RequestBoardDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static clone.gozik.entity.CompanyTypeEnum.*;

@Entity
@Getter
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String image;

    @Column
    private String logo;

    @Column
    private String nickname;

    @Column
    private CompanyTypeEnum companyType;

    @Column
    private RecruitTypeEnum recruitmentperiod;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate lastDate;

    @Column
    private Integer viewCount;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnoreProperties({"board"})
    private List<Job> job =new ArrayList<>();

    @ManyToOne
    private Member member;

    @OneToOne(mappedBy = "board", cascade = CascadeType.REMOVE)
    private LogoAndImage logoAndImage;

    //상시채용이 아닐경우 lastdate를 확인하여 넣어줌
    public Board(RequestBoardDto requestBoardDto, String nickname,LocalDate lastDate, LocalDate startDate, Member member,String image,String logo) {
        this.title = requestBoardDto.getTitle();
        this.description = requestBoardDto.getDescription();
        this.nickname = nickname;
        this.startDate = startDate;
        this.lastDate = lastDate;
        this.viewCount = 0;
        this.member = member;
        this.recruitmentperiod = RecruitTypeEnum.OPEN;
        this.logo=logo;
        this.image=image;
        this.companyType = switch (requestBoardDto.getCompanytype()){
            case "대기업" -> BIG;
            case "중견기업" ->MIDDLE;
            case "공공기관" ->PUBLIC;
            case "기타기업" -> ECT;
            default -> throw new IllegalArgumentException("회사 형태가 정확하지 않습니다");
        };
    }
    //상시채용일경우 lastdate = null
    public Board(RequestBoardDto requestBoardDto, String nickname, LocalDate startDate, Member member,String image,String logo) {
        this.title = requestBoardDto.getTitle();
        this.description = requestBoardDto.getDescription();
        this.nickname = nickname;
        this.startDate = startDate;
        this.viewCount = 0;
        this.member = member;
        this.recruitmentperiod = RecruitTypeEnum.ONGOING;
        this.logo=logo;
        this.image=image;
        this.companyType = switch (requestBoardDto.getCompanytype()){
            case "대기업" -> BIG;
            case "중견기업" ->MIDDLE;
            case "공공기관" ->PUBLIC;
            case "기타기업" -> ECT;
            default -> throw new IllegalArgumentException("회사 형태가 정확하지 않습니다");
        };
    }
    public void closed(){
        this.recruitmentperiod=RecruitTypeEnum.CLOSED;
    }
    public void update(RequestBoardDto requestBoardDto,LocalDate startDate, Member member,String image,String logo){
        this.title = requestBoardDto.getTitle();
        this.description = requestBoardDto.getDescription();
        this.startDate = startDate;
        this.viewCount = 0;
        this.member = member;
        this.recruitmentperiod = RecruitTypeEnum.ONGOING;
        this.logo=logo;
        this.image=image;
        this.companyType = switch (requestBoardDto.getCompanytype()){
            case "대기업" -> BIG;
            case "중견기업" ->MIDDLE;
            case "공공기관" ->PUBLIC;
            case "기타기업" -> ECT;
            default -> throw new IllegalArgumentException("회사 형태가 정확하지 않습니다");
        };
    }
    public void update(RequestBoardDto requestBoardDto,LocalDate lastDate,LocalDate startDate, Member member,String image,String logo){
        this.title = requestBoardDto.getTitle();
        this.description = requestBoardDto.getDescription();
        this.startDate = startDate;
        this.lastDate=lastDate;
        this.viewCount = 0;
        this.member = member;
        this.recruitmentperiod = RecruitTypeEnum.OPEN;
        this.logo=logo;
        this.image=image;
        this.companyType = switch (requestBoardDto.getCompanytype()){
            case "대기업" -> BIG;
            case "중견기업" ->MIDDLE;
            case "공공기관" ->PUBLIC;
            case "기타기업" -> ECT;
            default -> throw new IllegalArgumentException("회사 형태가 정확하지 않습니다");
        };
    }
}
