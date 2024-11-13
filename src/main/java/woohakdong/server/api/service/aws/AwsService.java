package woohakdong.server.api.service.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwsService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String BUCKET_NAME;

    public String generatePresignedUrl(String key, Date expiration) {
        GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, key);
        presignedUrlRequest.setMethod(HttpMethod.PUT);
        presignedUrlRequest.setExpiration(expiration);
        return amazonS3.generatePresignedUrl(presignedUrlRequest).toExternalForm();
    }
}
