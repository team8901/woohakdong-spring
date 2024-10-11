package woohakdong.server.api.controller.club.dto;

import lombok.NonNull;

public record ClubAccountValidateRequest(
        @NonNull
        String clubAccountBankName,

        @NonNull
        String clubAccountNumber
) {
}
