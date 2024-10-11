package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import lombok.NonNull;
import woohakdong.server.domain.club.Club;

@Builder
public record ClubCreateRequest(
        @NonNull
        String clubName,

        @NonNull
        String clubEnglishName,

        String clubImage,
        String clubDescription,
        String clubRoom,

        String clubGeneration,
        int clubDues
){
}
