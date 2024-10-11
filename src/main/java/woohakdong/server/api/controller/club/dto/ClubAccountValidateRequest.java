package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ClubAccountValidateRequest(
        @NonNull
        String clubAccountBankName,

        @NonNull
        String clubAccountNumber
) {
}
