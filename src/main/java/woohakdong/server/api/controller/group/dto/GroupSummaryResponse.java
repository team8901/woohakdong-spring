package woohakdong.server.api.controller.group.dto;

import lombok.Builder;
import woohakdong.server.domain.group.Group;

@Builder
public record GroupSummaryResponse(
        Long groupId,
        String groupName,
        String groupDescription,
        String groupJoinLink,
        Integer groupAmount
) {
    public static GroupSummaryResponse from(Group group) {
        return GroupSummaryResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupDescription())
                .groupJoinLink(group.getGroupJoinLink())
                .groupAmount(group.getGroupAmount())
                .build();
    }
}
