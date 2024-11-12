package woohakdong.server.api.controller.dues.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoResponse;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.member.Member;

import java.time.LocalDateTime;

@Builder
public record ClubAccountHistoryListResponse(
        Long clubAccountHistoryId,
        AccountType clubAccountHistoryInOutType,
        LocalDateTime clubAccountHistoryTranDate,
        Long clubAccountHistoryBalanceAmount,
        Long clubAccountHistoryTranAmount,
        String clubAccountHistoryContent
) {
    public static ClubAccountHistoryListResponse from(ClubAccountHistory history) {
        return ClubAccountHistoryListResponse.builder()
                .clubAccountHistoryId(history.getClubAccountHistoryId())
                .clubAccountHistoryInOutType(history.getClubAccountHistoryInOutType())
                .clubAccountHistoryTranDate(history.getClubAccountHistoryTranDate())
                .clubAccountHistoryBalanceAmount(history.getClubAccountHistoryBalanceAmount())
                .clubAccountHistoryTranAmount(history.getClubAccountHistoryTranAmount())
                .clubAccountHistoryContent(history.getClubAccountHistoryContent())
                .build();
    }
}
