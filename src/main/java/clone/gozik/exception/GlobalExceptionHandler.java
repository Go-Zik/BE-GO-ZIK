package clone.gozik.exception;

import clone.gozik.dto.MessageDto;
import clone.gozik.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<MessageDto> handleCustomException(CustomException exception) {
        log.error("CustomException throw Exception : {}", exception.getErrorCode());
        return MessageDto.toResponseEntity(exception.getErrorCode());
    }

}
