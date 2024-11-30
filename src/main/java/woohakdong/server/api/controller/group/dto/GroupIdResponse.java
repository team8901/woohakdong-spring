package woohakdong.server.api.controller.group.dto;

import lombok.Builder;
import woohakdong.server.domain.group.Group;

@Builder
public record GroupIdResponse(
        Long groupId
) {
    public static GroupIdResponse from(Group group) {
        return GroupIdResponse.builder()
                .groupId(group.getGroupId())
                .build();
    }
}
