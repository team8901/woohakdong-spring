package woohakdong.server.common.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<ErrorResponseBody<String>> handleCustomException(CustomException ex) {
        CustomErrorInfo customErrorInfo = ex.getCustomErrorInfo();
        ErrorResponseBody<String> errorResponseBody = new ErrorResponseBody<>(customErrorInfo.getDetailStatusCode(),
                customErrorInfo.getMessage());

        log.error("Exception : {}", customErrorInfo.getMessage());

        return ResponseEntity
                .status(customErrorInfo.getStatusCode())
                .body(errorResponseBody);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody<List>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errorMessages = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(fieldName + " : " + errorMessage);
        });

        ErrorResponseBody<List> errorResponseBody = new ErrorResponseBody<>(HttpStatus.BAD_REQUEST.value(), errorMessages);
        return new ResponseEntity<>(errorResponseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public void handleValidationExceptions(Exception ex) {
        log.error("Exception : {}", ex.getMessage());
    }
}
