package woohakdong.server.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("memberProvideId로 멤버를 찾을 수 있다.")
    @Test
    void existByMemberProvideId() {
        // Given
        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .memberPhoneNumber("01012345678")
                .memberMajor("Computer Science")
                .memberStudentNumber("20210001")
                .memberGender(MemberGender.MAN)
                .build();

        memberRepository.save(member);

        // When
        Optional<Member> foundMember = memberRepository.findByMemberProvideId(provideId);

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getMemberProvideId()).isEqualTo(provideId);
    }
}