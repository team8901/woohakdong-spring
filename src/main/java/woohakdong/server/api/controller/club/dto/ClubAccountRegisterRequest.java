package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

@Builder
public record ClubAccountRegisterRequest(
        String clubAccountBankName,
        String clubAccountNumber,
        String clubAccountPinTechNumber
) {
}
