package woohakdong.server.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.school.School;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberProvideId(String provideId);

    boolean existsByMemberProvideId(String provideId);

    Long countBySchool(School school);
}
