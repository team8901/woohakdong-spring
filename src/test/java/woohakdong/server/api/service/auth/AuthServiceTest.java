package woohakdong.server.api.service.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@ActiveProfiles("test")
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AuthService authService;

    @DisplayName("학교 도메인인 구글 이메일로만 로그인 할 수 있다.")
    @Test
    void schoolDomainValidation() {
        // given
        String email = "uiyeop@ajou.ac.kr";

        //when
        School school = authService.checkSchoolDomain(email);

        //then
        assertThat(school).isNotNull();
        assertThat(school.getSchoolDomain()).isEqualTo("ajou.ac.kr");


    }

    @DisplayName("학교 도메인이 아닌 구글 이메일로는 로그인 할 수 없다.")
    @Test
    void schoolDomainInValidation() {
        // given
        String email = "uiyeop@gmail.com";

        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkSchoolDomain(email));
        assertThat(exception.getCustomErrorInfo()).isEqualTo(INVALID_SCHOOL_DOMAIN);
    }
}