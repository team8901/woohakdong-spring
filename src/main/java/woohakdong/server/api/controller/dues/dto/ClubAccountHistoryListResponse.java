package woohakdong.server.api.controller.dues.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import woohakdong.server.domain.admin.adminAccount.AccountType;

import java.time.LocalDateTime;

public record ClubAccountHistoryListResponse(
        Long clubAccountHistoryId,
        AccountType clubAccountHistoryInOutType,
        LocalDateTime clubAccountHistoryTranDate,
        Long clubAccountHistoryBalanceAmount,
        Long clubAccountHistoryTranAmount,
        String clubAccountHistoryContent
) {
}
