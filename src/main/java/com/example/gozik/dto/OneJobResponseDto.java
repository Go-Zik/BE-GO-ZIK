package clone.gozic.dto;

import clone.gozic.entity.EmployeeType;
import clone.gozic.entity.Job;

public class OneJobResponseDto {
    private EmployeeType imcruitType;
    private String jobDetail;

    public OneJobResponseDto(Job job) {
        this.imcruitType = job.getIncruittype();
        this.jobDetail = job.getJobDetail();
    }
}
