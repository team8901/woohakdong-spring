package woohakdong.server.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_SCHOOL_DOMAIN;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.api.service.SecurityContextSetUp;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

class AuthServiceTest extends SecurityContextSetUp {

    @Autowired
    private AuthService authService;

    @Autowired
    private SchoolRepository schoolRepository;

    @BeforeEach
    void setUp() {
        school = createSchool("ajou.ac.kr");
    }

    private School school;

    @DisplayName("학교 도메인인 구글 이메일로만 로그인 할 수 있다.")
    @Test
    void schoolDomainValidation() {
        // given
        String email = "uiyeop@ajou.ac.kr";

        //when
        School school = authService.checkSchoolDomain(email);

        //then
        assertThat(school).isNotNull()
                        .extracting("schoolDomain")
                        .isEqualTo("ajou.ac.kr");
    }

    @DisplayName("학교 도메인이 아닌 구글 이메일로는 로그인 할 수 없다.")
    @Test
    void schoolDomainInValidation() {
        // given
        String email = "uiyeop@gmail.com";

        //when & then
        assertThatThrownBy(() -> authService.checkSchoolDomain(email))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_SCHOOL_DOMAIN.getMessage());
    }

    private School createSchool(String domain) {
        School school = School.builder()
                .schoolName("아주대학교")
                .schoolDomain(domain)
                .build();
        return schoolRepository.save(school);
    }
}