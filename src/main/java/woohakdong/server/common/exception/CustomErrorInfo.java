package woohakdong.server.common.exception;

import lombok.Getter;

@Getter
public enum CustomErrorInfo {

    // 400 BAD_REQUEST
    UTIL_IMAGE_COUNT_INVALID(400, "Image count should be greater than 0", 400001),

    // 401 UNAUTHORIZED

    // 403 FORBIDDEN

    // 404 NOT_FOUND

    // 409 CONFLICT

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(500, "Internal server error", 500001);

    private final int statusCode;
    private final String message;
    private final int detailStatusCode;

    CustomErrorInfo(int statusCode, String message, int detailStatusCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.detailStatusCode = detailStatusCode;
    }
}
