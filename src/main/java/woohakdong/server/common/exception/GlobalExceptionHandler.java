package woohakdong.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<ErrorResponseBody> handleCustomException(CustomException ex) {
        CustomErrorInfo customErrorInfo = ex.getCustomErrorInfo();
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(customErrorInfo.getDetailStatusCode(), customErrorInfo.getMessage());

        log.error("Exception : {}", customErrorInfo.getMessage());

        return ResponseEntity
                .status(customErrorInfo.getStatusCode())
                .body(errorResponseBody);
    }

    @ExceptionHandler(Exception.class)
    public void handleValidationExceptions(Exception ex) {
        log.error("Exception : {}", ex.getMessage());
    }
}
