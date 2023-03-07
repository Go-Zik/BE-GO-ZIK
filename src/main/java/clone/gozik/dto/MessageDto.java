package clone.gozik.dto;

import clone.gozik.entity.ErrorCode;
import clone.gozik.entity.SuccessCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;


@Getter
public class MessageDto {
    private String message;
    private int statusCode;
    @Builder
    public MessageDto(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public static MessageDto of(SuccessCode successCode) {
        return MessageDto.builder()
                .statusCode(successCode.getHttpStatus().value())
                .message(successCode.getMsg())
                .build();
    }

    public static MessageDto of(ErrorCode errorCode) {
        return MessageDto.builder()
                .statusCode(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }

    public static ResponseEntity<MessageDto> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(MessageDto.builder()
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .build());
    }
}
