package clone.gozik.dto;

import clone.gozik.entity.Job;
import lombok.Getter;

@Getter
public class AllJobResponseDto {
    private String incruittype;

    public AllJobResponseDto(Job job){
        incruittype = job.getIncruittype();
    }
}
