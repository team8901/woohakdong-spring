package woohakdong.server.api.controller.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GroupUpdateRequest(
        @NotNull
        String groupName,
        String groupDescription,
        String groupChatLink,
        String groupChatPassword,
        @NotNull
        Boolean groupIsActivated,
        @NotNull
        Integer groupMemberLimit
) {
}
