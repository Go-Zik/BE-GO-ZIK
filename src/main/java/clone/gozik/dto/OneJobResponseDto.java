package clone.gozik.dto;

import clone.gozik.entity.Job;
import lombok.Getter;


@Getter
public class OneJobResponseDto {
    private String incruittype;
    private String jobdetail;

    public OneJobResponseDto(Job job) {
        this.incruittype = job.getIncruittype();
        this.jobdetail = job.getJobDetail();
    }
}
