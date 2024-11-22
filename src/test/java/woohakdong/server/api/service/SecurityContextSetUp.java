package woohakdong.server.api.service;

import static woohakdong.server.config.TestConstants.TEST_PROVIDE_ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.config.WithMockCustomUser;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@WithMockCustomUser
public abstract class SecurityContextSetUp {

    @Autowired
    private MemberRepository memberRepository;

    public Member createExampleMember() {
        Member member = Member.builder()
                .memberProvideId(TEST_PROVIDE_ID)
                .memberName("john doe")
                .memberEmail("johnDoe@example.com")
                .build();
        return memberRepository.save(member);
    }
}
