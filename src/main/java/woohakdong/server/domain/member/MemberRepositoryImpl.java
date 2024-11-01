package woohakdong.server.domain.member;

import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Member getById(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    @Override
    public Optional<Member> findByMemberProvideId(String memberProvideId) {
        return memberJpaRepository.findByMemberProvideId(memberProvideId);
    }
}
