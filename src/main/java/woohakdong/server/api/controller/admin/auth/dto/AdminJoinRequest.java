package woohakdong.server.api.controller.admin.auth.dto;

public record AdminJoinRequest(
        String memberLoginId,
        String memberName,
        String memberEmail
) {
}
