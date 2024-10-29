package woohakdong.server.api.controller.club.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ClubUpdateRequest(
        String clubImage,

        @NotNull(message = "동아리 설명은 필수 입력 값입니다.")
        String clubDescription,

        String clubRoom,

        String clubGeneration,

        @NotNull(message = "동아리 단체 채팅 링크는 필수 입력 값입니다.")
        String clubGroupChatLink,

        String clubGroupChatPassword,

        @NotNull(message = "회비는 필수 입력 값입니다.")
        Integer clubDues
) {
}