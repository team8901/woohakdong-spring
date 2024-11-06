package woohakdong.server.api.controller.admin.auth.dto;

public record AdminInfoUpdateRequest(
        String username,
        String name,
        String email,
        String password
) {
}
