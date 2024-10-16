package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

@Builder
public record ClubJoinGroupInfoResponse(
        Long groupId,
        String groupName,
        String groupLink,
        String groupDescription,
        Integer groupAmount
) {
}
