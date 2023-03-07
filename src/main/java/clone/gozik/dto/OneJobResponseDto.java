package clone.gozik.dto;

import clone.gozik.entity.Job;
import lombok.Getter;


@Getter
public class OneJobResponseDto {
    private String incruitType;
    private String jobDetail;

    public OneJobResponseDto(Job job) {
        this.incruitType = job.getIncruittype();
        this.jobDetail = job.getJobDetail();
    }
}
