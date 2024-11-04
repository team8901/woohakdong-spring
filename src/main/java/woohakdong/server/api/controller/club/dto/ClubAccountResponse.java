package woohakdong.server.api.controller.club.dto;

import jakarta.persistence.Column;
import lombok.Builder;

import java.time.LocalDateTime;

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
}
