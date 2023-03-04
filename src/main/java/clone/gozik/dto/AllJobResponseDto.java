package clone.gozik.dto;

import clone.gozik.entity.EmployeeType;
import clone.gozik.entity.Job;
import lombok.Getter;

@Getter
public class AllJobResponseDto {
    private EmployeeType incruittype;

    public AllJobResponseDto(Job job){
        incruittype = job.getIncruittype();
    }
}
