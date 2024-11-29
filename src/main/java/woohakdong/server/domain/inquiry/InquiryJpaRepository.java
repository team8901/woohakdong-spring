package woohakdong.server.domain.inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.member.Member;

import java.util.List;

public interface InquiryJpaRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMember(Member member);
}
