package clone.gozik.dto;

import clone.gozik.entity.CompanyTypeEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestBoardDto {
    private String title;
    private String description;
    private CompanyTypeEnum companytype;
    private String startDate;
    private String endDate;
    private boolean recruitmentPeriod;
    private List job;
}
