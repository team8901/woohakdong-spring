package woohakdong.server.api.service.inquiry;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.api.controller.inquiry.dto.InquiryRequest;
import woohakdong.server.api.controller.inquiry.dto.InquiryResponse;
import woohakdong.server.SecurityContextSetup;
import woohakdong.server.domain.inquiry.Inquiry;
import woohakdong.server.domain.inquiry.InquiryCategory;
import woohakdong.server.domain.inquiry.InquiryRepository;

import java.util.List;
import org.junit.jupiter.api.Test;
import woohakdong.server.domain.member.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;


class InquiryServiceTest extends SecurityContextSetup {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private InquiryRepository inquiryRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createExampleMember();
    }

    @DisplayName("문의를 생성할 수 있다.")
    @Test
    void createInquiry() {
        // Given
        InquiryRequest request = new InquiryRequest("Test inquiry content", InquiryCategory.INQUIRY);

        // When
        inquiryService.createInquiry(request);

        // Then
        List<Inquiry> inquiries = inquiryRepository.getAll();
        assertThat(inquiries).hasSize(1)
                .extracting(Inquiry::getInquiryContent, Inquiry::getInquiryCategory)
                .containsExactly(tuple("Test inquiry content", InquiryCategory.INQUIRY));
    }

    @DisplayName("자신의 문의 내역을 불러올 수 있다.")
    @Test
    void getInquiries() {
        // Given
        createExampleInquiry("Test inquiry content 1", InquiryCategory.INQUIRY, member);
        createExampleInquiry("Test inquiry content 2", InquiryCategory.INQUIRY, member);

        // When
        List<InquiryResponse> responses = inquiryService.getInquiries();

        // Then
        assertThat(responses).hasSize(2)
                .extracting(InquiryResponse::inquiryContent, InquiryResponse::inquiryCategory)
                .containsExactly(
                        tuple("Test inquiry content 1", InquiryCategory.INQUIRY),
                        tuple("Test inquiry content 2", InquiryCategory.INQUIRY)
                );
    }

    private Inquiry createExampleInquiry(String inquiryContent, InquiryCategory category, Member member) {
        Inquiry inquiry = Inquiry.builder()
                .InquiryContent(inquiryContent)
                .inquiryCategory(category)
                .member(member)
                .build();

        inquiryRepository.save(inquiry);
        return inquiry;
    }
}