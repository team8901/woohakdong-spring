package woohakdong.server.domain.inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.member.Member;

import java.util.List;

public interface InquiryJpaRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMember(Member member);

    @Query("SELECT i FROM Inquiry i " +
            "WHERE (:category IS NULL OR i.inquiryCategory = :category) " +
            "ORDER BY i.createdAt DESC")
    List<Inquiry> findByCategoryOrderByCreatedAtDesc(@Param("category") InquiryCategory category);
}
