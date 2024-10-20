package woohakdong.server.api.controller.club.dto;

import lombok.NonNull;

public record ClubNameValidateRequest(
        @NonNull
        String clubName,

        @NonNull
        String clubEnglishName
) {
}
