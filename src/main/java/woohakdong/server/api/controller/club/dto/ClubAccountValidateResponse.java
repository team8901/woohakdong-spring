package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

@Builder
public record ClubAccountValidateResponse(
        String clubAccountBankName,
        String clubAccountNumber,
        String clubAccountPinTechNumber
) {
}
