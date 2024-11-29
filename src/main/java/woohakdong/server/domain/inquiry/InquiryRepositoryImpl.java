package woohakdong.server.domain.inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.member.Member;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class InquiryRepositoryImpl implements InquiryRepository {

    private final InquiryJpaRepository inquiryJpaRepository;

    @Override
    public Inquiry save(Inquiry inquiry) {
        return inquiryJpaRepository.save(inquiry);
    }

    @Override
    public List<Inquiry> getByMember(Member member) {
        return inquiryJpaRepository.findByMember(member);
    }

    @Override
    public List<Inquiry> getAll() {
        return inquiryJpaRepository.findAll();
    }
}
