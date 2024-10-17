package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import woohakdong.server.domain.group.Group;

@Builder
public record ClubJoinGroupInfoResponse(
        Long groupId,
        String groupName,
        String groupLink,
        String groupDescription,
        Integer groupAmount
) {

    public static ClubJoinGroupInfoResponse from(Group group) {
        return ClubJoinGroupInfoResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupLink(group.getGroupLink())
                .groupDescription(group.getGroupDescription())
                .groupAmount(group.getGroupAmount())
                .build();
    }
}
