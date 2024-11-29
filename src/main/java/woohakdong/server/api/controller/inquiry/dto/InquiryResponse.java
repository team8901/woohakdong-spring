package woohakdong.server.api.controller.inquiry.dto;

import lombok.Builder;
import woohakdong.server.domain.inquiry.Inquiry;
import woohakdong.server.domain.inquiry.InquiryCategory;

@Builder
public record InquiryResponse(
        Long inquiryId,
        String inquiryContent,
        InquiryCategory inquiryCategory
) {
    static public InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .inquiryId(inquiry.getInquiryId())
                .inquiryContent(inquiry.getInquiryContent())
                .inquiryCategory(inquiry.getInquiryCategory())
                .build();
    }
}
