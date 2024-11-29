package woohakdong.server.domain.inquiry;

import woohakdong.server.domain.member.Member;

import java.util.List;

public interface InquiryRepository {

    Inquiry save(Inquiry inquiry);
    List<Inquiry> getByMember(Member member);
    List<Inquiry> getAll();
}
