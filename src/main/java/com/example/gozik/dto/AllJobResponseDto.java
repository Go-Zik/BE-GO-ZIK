package clone.gozic.dto;

import clone.gozic.entity.EmployeeType;
import clone.gozic.entity.Job;
import lombok.Getter;

@Getter
public class AllJobResponseDto {
    private EmployeeType incruittype;

    public AllJobResponseDto(Job job){
        incruittype = job.getIncruittype();
    }
}
