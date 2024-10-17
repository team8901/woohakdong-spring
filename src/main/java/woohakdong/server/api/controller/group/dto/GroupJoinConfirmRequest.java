package woohakdong.server.api.controller.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GroupJoinConfirmRequest(

        @Schema(description = "주문 생성 시, 클라이언트 측에서 생성한 주문 번호", example = "abc123")
        @NotBlank(message = "merchantUid은 null이 될 수 없습니다.")
        String merchantUid,

        @Schema(description = "아임포트에서 발급한 고유 주문 번호", example = "imp123")
        @NotNull(message = "impUid은 null이 될 수 없습니다.")
        String impUid,

        Long orderId
) {
}
