package woohakdong.server.common.exception;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<ErrorResponseBody> handleCustomException(HttpServletRequest request, CustomException ex) {
        CustomErrorInfo customErrorInfo = ex.getCustomErrorInfo();
        ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
                .detailStatusCode(customErrorInfo.getStatusCode())
                .message(customErrorInfo.getMessage())
                .build();

        log.error("\tException at {}", request.getRequestURI());
        log.error("\tCustomException : {}\n", customErrorInfo.getMessage());

        return ResponseEntity
                .status(customErrorInfo.getStatusCode())
                .body(errorResponseBody);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity handleValidationExceptions(HttpServletRequest request, Exception ex) {
        Sentry.captureException(ex);

        log.error("\tException at {}", request.getRequestURI());
        log.error("\tUnhandled Exception : {}\n", ex.getMessage());

        return ResponseEntity
                .status(500)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity handleValidationExceptions(HttpServletRequest request, MethodArgumentNotValidException ex) {
        ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
                .detailStatusCode(400)
                .message(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage())
                .build();

        log.error("\tException at {}", request.getRequestURI());
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            log.error("\tValidation Error : {} -> {}\n", error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity
                .status(400)
                .body(errorResponseBody);
    }
}
