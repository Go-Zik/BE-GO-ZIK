package clone.gozik.dto;

import clone.gozik.entity.EmployeeType;
import clone.gozik.entity.Job;
import lombok.Getter;


@Getter
public class OneJobResponseDto {
    private EmployeeType incruitType;
    private String jobDetail;

    public OneJobResponseDto(Job job) {
        this.incruitType = job.getIncruittype();
        this.jobDetail = job.getJobDetail();
    }
}
