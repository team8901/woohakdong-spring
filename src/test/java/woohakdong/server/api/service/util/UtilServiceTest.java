package woohakdong.server.api.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.UTIL_IMAGE_COUNT_INVALID;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.api.controller.util.dto.S3PresignedUrlResponse;
import woohakdong.server.SecurityContextSetup;
import woohakdong.server.common.exception.CustomException;

class UtilServiceTest extends SecurityContextSetup {

    @Autowired
    private UtilService utilService;

    @BeforeEach
    void setUp() {
        createExampleMember();
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