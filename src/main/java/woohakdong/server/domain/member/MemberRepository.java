package woohakdong.server.domain.member;

import woohakdong.server.domain.school.School;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Member getById(Long memberId);

    Optional<Member> findByMemberProvideId(String memberProvideId);

    Member findByAdminMemberProvideId(String memberProvideId);

    boolean findByDuplicateMemberProvideId(String memberProvideId);

    Long count();

    Long countBySchool(School school);
}
