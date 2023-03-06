package clone.gozik.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_EMAIL(BAD_REQUEST, "중복된 이메일이 있습니다."),
    DUPLICATE_NICKNAME(BAD_REQUEST, "중복된 회사명이 있습니다."),

    UNREGISTER_EMAIL(BAD_REQUEST, "등록되지 않은 이메일입니다."),
    INVALID_PASSWORD(BAD_REQUEST, "비밀번호가 입력되지 않았습니다."),

    NULL_BOARD_DATA(BAD_REQUEST, "해당 게시글이 없습니다."),
    NULL_IMAGE_DATA(BAD_REQUEST, "필요한 이미지가 없습니다."),
    NOT_RECRUIT_TYPE(UNAUTHORIZED, "수시채용이 아닙니다"),


    /* 401 UNAUTHORIZED : 인증 실패 */
    NOT_AUTHOR(UNAUTHORIZED, "not author"),
    INVALID_TOKEN(UNAUTHORIZED, "invalid token"),
    NULL_TOKEN(UNAUTHORIZED, "null token"),

    /* 403 FORBIDDEN : 인가 실패 */
    PERMISSION_DINED(FORBIDDEN, "forbidden");


    private final HttpStatus httpStatus;
    private final String message;
}
