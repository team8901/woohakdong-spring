package woohakdong.server.api.service.aws;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
class AwsServiceTest {

    @Autowired
    private AwsService awsService;
    private static final String BUCKET_NAME = "woohakdong.image";

    @DisplayName("Presigned URL 생성 테스트")
    @Test
    void generatePresignedUrl(){
        // Given
        String key = "testKey";
        LocalDate expiredLocalDate = LocalDate.of(2024, 10, 7);
        Date expiration = Date.from(expiredLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // When
        String presignedUrl = awsService.generatePresignedUrl(key, expiration);

        // Then
        assertThat(presignedUrl).isNotNull();
        assertThat(presignedUrl).contains(BUCKET_NAME);
        assertThat(presignedUrl).contains(key);
    }

}