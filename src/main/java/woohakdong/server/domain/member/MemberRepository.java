package woohakdong.server.domain.member;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Member getById(Long memberId);

    Optional<Member> findByMemberProvideId(String memberProvideId);
}
