package woohakdong.server.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_SCHOOL_DOMAIN;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private SchoolRepository schoolRepository;

    @DisplayName("학교 도메인인 구글 이메일로만 로그인 할 수 있다.")
    @Test
    void schoolDomainValidation() {
        // given
        createSchool();
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
        createSchool();
        String email = "uiyeop@gmail.com";

        //when & then
        assertThatThrownBy(() -> authService.checkSchoolDomain(email))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_SCHOOL_DOMAIN.getMessage());
    }

    private School createSchool() {
        School school = School.builder()
                .schoolName("아주대학교")
                .schoolDomain("ajou.ac.kr")
                .build();
        return schoolRepository.save(school);
    }
}