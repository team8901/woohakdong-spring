package woohakdong.server.api.controller.group.dto;

public record GroupInfoResponse(
        Long groupId,
        String groupName,
        String groupJoinLink,
        String groupDescription,
        Integer groupAmount
) {
}
