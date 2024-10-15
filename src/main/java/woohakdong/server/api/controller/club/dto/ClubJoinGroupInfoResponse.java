package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

@Builder
public record ClubJoinGroupInfoResponse(
        int groupId,
        String groupName,
        String groupLink,
        String groupDescription,
        int groupAmount
) {
}
