package woohakdong.server.common.exception;

import lombok.Getter;

@Getter
public enum CustomErrorInfo {

    // 400 BAD_REQUEST
    REFRESH_TOKEN_IS_NULL(400, "refresh token null", 400001),
    REFRESH_TOKEN_EXPIRED(400, "refresh token expired", 400002),
    INVALID_REFRESH_TOKEN(400, "invalid refresh token", 400003),
    ALREADY_EXISTS(400, "already exists", 400004),

    // 401 UNAUTHORIZED
    INVALID_ACCESS_TOKEN(401, "Invaild access token", 401001),
    INVALID_SCHOOL_DOMAIN(401, "Invalid school domain", 401002),
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
