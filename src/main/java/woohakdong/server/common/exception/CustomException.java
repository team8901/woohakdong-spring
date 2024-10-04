package woohakdong.server.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomErrorInfo customErrorInfo;

    public CustomException(CustomErrorInfo customErrorInfo) {
        this.customErrorInfo = customErrorInfo;
    }
}
