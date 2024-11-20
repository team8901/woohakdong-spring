package woohakdong.server.api.controller.group.dto;

import lombok.Builder;
import woohakdong.server.domain.group.Group;

@Builder
public record GroupInfoResponse(
        Long groupId,
        String groupName,
        String groupJoinLink,
        String groupDescription,
        Integer groupAmount
) {
    public static GroupInfoResponse from(Group group) {
        return GroupInfoResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupJoinLink(group.getGroupJoinLink())
                .groupDescription(group.getGroupDescription())
                .groupAmount(group.getGroupAmount())
                .build();
    }
}
