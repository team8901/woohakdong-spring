package woohakdong.server.api.controller.admin.auth.dto;

import lombok.Builder;

@Builder
public record AdminInfoResponse(
        String username,
        String name,
        String email
) {
}
