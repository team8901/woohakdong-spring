package woohakdong.server.api.service.inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.inquiry.dto.InquiryRequest;
import woohakdong.server.api.controller.inquiry.dto.InquiryResponse;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.inquiry.Inquiry;
import woohakdong.server.domain.inquiry.InquiryRepository;
import woohakdong.server.domain.member.Member;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryService {

    private final SecurityUtil securityUtil;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void createInquiry(InquiryRequest inquiryRequest) {
        Member member = securityUtil.getMember();

        Inquiry inquiry = Inquiry.create(inquiryRequest.inquiryContent(), inquiryRequest.inquiryCategory(), member);
        inquiryRepository.save(inquiry);
    }

    public List<InquiryResponse> getInquiries() {
        Member member = securityUtil.getMember();

        List<Inquiry> inquiries = inquiryRepository.getByMember(member);
        return inquiries.stream()
                .map(inquiry -> InquiryResponse.from(inquiry))
                .collect(Collectors.toList());
    }
}
