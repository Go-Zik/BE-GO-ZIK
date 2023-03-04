package clone.gozik.dto;

import clone.gozik.entity.EmployeeType;
import clone.gozik.entity.Job;

public class OneJobResponseDto {
    private EmployeeType imcruitType;
    private String jobDetail;

    public OneJobResponseDto(Job job) {
        this.imcruitType = job.getIncruittype();
        this.jobDetail = job.getJobDetail();
    }
}
