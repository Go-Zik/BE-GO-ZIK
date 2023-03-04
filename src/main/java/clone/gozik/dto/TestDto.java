package clone.gozik.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDto {

    private String msg;

    private String code;

    public TestDto(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }
}
