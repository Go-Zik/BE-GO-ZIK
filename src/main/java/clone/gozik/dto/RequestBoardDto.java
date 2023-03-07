package clone.gozik.dto;

import clone.gozik.entity.CompanyTypeEnum;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
public class RequestBoardDto {
    private String title;
    private String description;
    private String companytype;
    private String startdate;
    @Nullable
    private String enddate;
    private boolean recruitmentperiod;
    @Nullable
    private List<RequestJobDto> job;



}
