package woohakdong.server.api.controller.club.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import woohakdong.server.domain.clubAccount.ClubAccount;

@Builder
public record ClubAccountResponse(
        Long clubAccountId,
        String clubAccountBankName,
        String clubAccountNumber,
        String clubAccountPinTechNumber,
        LocalDateTime clubAccountLastUpdateDate,
        String clubAccountBankCode,
        Long clubAccountBalance
) {
    public static ClubAccountResponse from(ClubAccount clubAccount) {
        return ClubAccountResponse.builder()
                .clubAccountId(clubAccount.getClubAccountId())
                .clubAccountBankName(clubAccount.getClubAccountBankName())
                .clubAccountNumber(clubAccount.getClubAccountNumber())
                .clubAccountPinTechNumber(clubAccount.getClubAccountPinTechNumber())
                .clubAccountLastUpdateDate(clubAccount.getClubAccountLastUpdateDate())
                .clubAccountBankCode(clubAccount.getClubAccountBankCode())
                .clubAccountBalance(clubAccount.getClubAccountBalance())
                .build();
    }
}
