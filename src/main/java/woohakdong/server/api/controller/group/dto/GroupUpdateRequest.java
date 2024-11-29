package woohakdong.server.api.controller.group.dto;

import jakarta.validation.constraints.NotNull;

public record GroupUpdateRequest(
        @NotNull
        String groupName,
        String groupDescription,
        String groupChatLink,
        String groupChatPassword,
        @NotNull
        Boolean groupIsActivated
) {
}
