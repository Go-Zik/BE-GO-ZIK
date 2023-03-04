package clone.gozic.dto;

import clone.gozic.entity.Board;
import clone.gozic.entity.CompanyTypeEnum;
import clone.gozic.entity.RecruitTypeEnum;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class AllBoardResponseDto {
    private Long id;
    private String nickname;
    private LocalDate startDate;
    private LocalDate lastDate;
    private RecruitTypeEnum recruitmentPeriod;
    private CompanyTypeEnum companyType;

    private Integer viewCount;

    private List incruteType;

    public AllBoardResponseDto(Board board,List incruteType) {
        this.id = board.getId();
        this.nickname = board.getNickname();
        this.startDate = board.getStartDate();
        this.lastDate = board.getLastDate();
        this.recruitmentPeriod = board.getRecruitmentperiod();
        this.companyType = board.getCompanyType();
        this.viewCount = board.getViewCount();
        this.incruteType = incruteType;
    }
}
