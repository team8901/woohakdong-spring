package woohakdong.server.domain.inquiry;

import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.member.Member;

import java.util.List;

public interface InquiryRepository {

    Inquiry save(Inquiry inquiry);
    List<Inquiry> getByMember(Member member);
    List<Inquiry> getAll();
    List<Inquiry> getByCategoryOrderByCreatedAtDesc(@Param("category") InquiryCategory category);
}
