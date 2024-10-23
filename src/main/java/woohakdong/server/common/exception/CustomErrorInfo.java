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
    CLUB_NOT_FOUND(400, "club not found", 400010),
    CLUB_MEMBER_ROLE_NOT_ALLOWED(400, "club member role not allowed", 400011),
    GROUP_NOT_FOUND(400, "group not found", 400012),
    INVALID_BANK_NAME(400, "bank name invalid", 400013),
    TRANSFER_FAILED(400, "transfer failed", 400014),
    ADMIN_ACCOUNT_NOT_FOUND(400, "admin account not found", 400015),
    ORDER_NOT_FOUND(400, "order not found", 400016),
    ORDER_ALREADY_EXIST(400, "order already exist", 400017),
    CLUB_ALREADY_JOINED(400, "club already joined", 400018),
    GROUP_TYPE_NOT_FOUND(400, "group type not found", 400019),
    ITEM_NOT_FOUND(400, "item not found", 400020),
    ITEM_NOT_AVAILABLE(400, "item not available", 400021),
    ITEM_USING(400, "item using", 400022),
    ITEM_NOT_USING(400, "item not using", 400023),
    ITEM_HISTORY_NOT_FOUND(400, "item history not found", 400024),

    // 401 UNAUTHORIZED
    INVALID_ACCESS_TOKEN(401, "Invaild access token", 401001),
    INVALID_SCHOOL_DOMAIN(401, "Invalid school domain", 401002),
    // 403 FORBIDDEN

    // 404 NOT_FOUND

    // 409 CONFLICT
    CLUB_NAME_DUPLICATION(409, "Duplicate club name", 409001),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(500, "Internal server error", 500001),
    MAIL_SEND_ERROR(500, "Mail send error", 500002);

    private final int statusCode;
    private final String message;
    private final int detailStatusCode;

    CustomErrorInfo(int statusCode, String message, int detailStatusCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.detailStatusCode = detailStatusCode;
    }
}
