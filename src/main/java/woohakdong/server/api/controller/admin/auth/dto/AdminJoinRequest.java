package woohakdong.server.api.controller.admin.auth.dto;

public record AdminJoinRequest(
        String username,
        String name,
        String email
) {
}
