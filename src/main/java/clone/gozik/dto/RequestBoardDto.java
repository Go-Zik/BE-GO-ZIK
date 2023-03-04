package clone.gozik.dto;

import clone.gozik.entity.CompanyTypeEnum;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
public class RequestBoardDto {
    @Nullable
    private String title;
    @Nullable
    private String description;
    @Nullable
    private String companytype;
    @Nullable
    private String startdate;
    private String enddate;
    @Nullable
    private boolean recruitmentperiod;
    @Nullable
    private List<RequestJobDto> job;
}