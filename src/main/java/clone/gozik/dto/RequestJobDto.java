package clone.gozik.dto;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class RequestJobDto {
    @Nullable
    private String incruittype;
    @Nullable
    private String jobdetail;
}
