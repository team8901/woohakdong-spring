package woohakdong.server.api.controller.admin.overall.dto;

import lombok.Builder;

@Builder
public record CountResponse(
        Long count
) {
    public static CountResponse from(Long count) {
        return CountResponse.builder()
                .count(count)
                .build();
    }
}
