package woohakdong.server.api.controller.admin.overall.dto;

import lombok.Builder;
import woohakdong.server.domain.inquiry.Inquiry;
import woohakdong.server.domain.inquiry.InquiryCategory;

import java.time.LocalDateTime;

@Builder
public record InquiryListResponse(
        Long inquiryId,
        String inquiryContent,
        InquiryCategory inquiryCategory,
        String memberEmail,
        LocalDateTime creatDate
) {
    static InquiryListResponse from(Inquiry inquiry, String memberEmail) {
        return InquiryListResponse.builder()
                .inquiryId(inquiry.getInquiryId())
                .inquiryContent(inquiry.getInquiryContent())
                .inquiryCategory(inquiry.getInquiryCategory())
                .memberEmail(memberEmail)
                .creatDate(inquiry.getCreatedAt())
                .build();
    }
}
