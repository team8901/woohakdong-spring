package woohakdong.server.api.service.util;

import static woohakdong.server.common.exception.CustomErrorInfo.UTIL_IMAGE_COUNT_INVALID;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import woohakdong.server.api.controller.util.dto.S3PresignedUrlResponse;
import woohakdong.server.api.service.aws.AwsService;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class UtilService {

    public static final Duration THREE_MINUTE = Duration.ofMinutes(3);

    private final SecurityUtil securityUtil;
    private final AwsService awsService;

    public List<S3PresignedUrlResponse> generatePresignedUrls(int imageCount) {

        if (imageCount <= 0) {
            throw new CustomException(UTIL_IMAGE_COUNT_INVALID);
        }

        Date expiration = Date.from(Instant.now().plus(THREE_MINUTE));
        List<S3PresignedUrlResponse> presignedUrlResponses = new ArrayList<>();

        for (int i = 0; i < imageCount; i++) {
            String keyPrefix = createKeyPrefix(i);
            String presignedUrl = awsService.generatePresignedUrl(keyPrefix, expiration);
            presignedUrlResponses.add(S3PresignedUrlResponse.of(presignedUrl));
        }

        return presignedUrlResponses;
    }

    private String createKeyPrefix(int i) {
        Long memberId = securityUtil.getMember().getMemberId();
        String dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-S-")
                .withZone(ZoneId.of("Asia/Seoul"))
                .format(Instant.now());
        return "user_" + memberId + "_" + dateTime + (i + 1);
    }
}
