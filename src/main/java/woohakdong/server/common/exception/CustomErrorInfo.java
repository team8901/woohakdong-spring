package woohakdong.server.common.exception;

import lombok.Getter;

@Getter
public enum CustomErrorInfo {

    // 400 BAD_REQUEST
    REFRESH_TOKEN_IS_NULL(400, "refresh token null", 400001),
    REFRESH_TOKEN_EXPIRED(400, "refresh token expired", 400002),
    INVALID_REFRESH_TOKEN(400, "invalid refresh token", 400003),
    REFRESH_TOKEN_ALREADY_EXISTS(400, "refresh token already exists", 400004),
    UTIL_IMAGE_COUNT_INVALID(400, "Image count should be greater than 0", 400005),
    MEMBER_NOT_FOUND(400, "member not found", 400006),
    SCHOOL_NOT_FOUND(400, "school not found", 400007),
    BANK_NOT_SUPPORTED(400, "bank not supported", 400008),
    BANK_INVALID_ACCOUNT_NUMBER(400, "account number invalid", 400009),

    // 401 UNAUTHORIZED
    INVALID_ACCESS_TOKEN(401, "Invaild access token", 401001),
    INVALID_SCHOOL_DOMAIN(401, "Invalid school domain", 401002),
    // 403 FORBIDDEN

    // 404 NOT_FOUND

    // 409 CONFLICT
    CLUB_NAME_DUPLICATION(409, "Duplicate club name", 409001),

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
