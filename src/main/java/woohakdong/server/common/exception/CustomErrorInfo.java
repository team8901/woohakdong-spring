package woohakdong.server.common.exception;

import lombok.Getter;

@Getter
public enum CustomErrorInfo {

    // 400 BAD_REQUEST

    // 401 UNAUTHORIZED
    INVALID_ACCESSTOKEN(401, "Invaild access token", 400001),
    INVALID_SCHOOL_DOMAIN(401, "Invalid school domain", 400002),
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
