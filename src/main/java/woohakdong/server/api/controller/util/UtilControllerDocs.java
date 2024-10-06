package woohakdong.server.api.controller.util;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.util.dto.S3PresignedUrlResponse;

@Tag(name = "Util", description = "유틸리티 API")
public interface UtilControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "이미지 업로드를 위한 URL 획득", description = "업로드할 이미지 개수를 입력하면, 그 개수만큼 URL을 제공합니다.")
    @ApiResponse(responseCode = "200", description = "S3 preSignedURL 획득 성공", useReturnTypeSchema = true)
    ListWrapperResponse<S3PresignedUrlResponse> getPreSignedUrl(
            @Schema(description = "이미지 개수", example = "2")
            int imageCount
    );

}
