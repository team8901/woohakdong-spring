package woohakdong.server.api.controller.inquiry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.inquiry.dto.InquiryRequest;
import woohakdong.server.api.controller.inquiry.dto.InquiryResponse;

@Tag(name = "Inquiry", description = "문의 관련 API")
public interface InquiryControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "문의 등록", description = "문의를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "문의 등록 성공", useReturnTypeSchema = true)
    void createInquiry(@RequestBody InquiryRequest inquiryRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "자신의 문의 리스트 반환", description = "자신의 문의 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "자신의 문의 리스트 반환 성공", useReturnTypeSchema = true)
    ListWrapperResponse<InquiryResponse> getInquiries();
}
