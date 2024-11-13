package woohakdong.server.api.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.UTIL_IMAGE_COUNT_INVALID;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.util.dto.S3PresignedUrlResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UtilServiceTest {

    @Autowired
    private UtilService utilService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        String provideId = "testProvideId";
        String role = "USER_ROLE";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("ImageCount 개수만큼 Presigned URL를 생성한다.")
    @Test
    void generatePresignedUrls() {
        // Given
        int imageCount = 3;

        // When
        List<S3PresignedUrlResponse> result = utilService.generatePresignedUrls(imageCount);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(imageCount);
    }

    @DisplayName("ImageCount가 1보다 작을 때 예외를 발생시킨다.")
    @Test
    void generatePresignedUrlsWithInvalidImageCount() {
        // Given
        int imageCount = 0;

        // When & Then
        assertThatThrownBy(() -> utilService.generatePresignedUrls(imageCount))
                .isInstanceOf(CustomException.class)
                .hasMessage(UTIL_IMAGE_COUNT_INVALID.getMessage());
    }
}