package woohakdong.server.api.controller.inquiry.dto;

import lombok.Builder;
import woohakdong.server.domain.inquiry.Inquiry;
import woohakdong.server.domain.inquiry.InquiryCategory;

@Builder
public record InquiryRequest(
        String inquiryContent,
        InquiryCategory inquiryCategory
) {
    static InquiryRequest from(Inquiry inquiry) {
        return InquiryRequest.builder()
                .inquiryContent(inquiry.getInquiryContent())
                .inquiryCategory(inquiry.getInquiryCategory())
                .build();
    }
}
