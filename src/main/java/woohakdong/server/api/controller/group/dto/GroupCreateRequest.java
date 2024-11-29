package woohakdong.server.api.controller.group.dto;

import jakarta.validation.constraints.NotNull;

public record GroupCreateRequest(
        @NotNull
        String groupName,
        String groupDescription,
        Integer groupAmount,
        String groupChatLink,
        String groupChatPassword
) {
}
