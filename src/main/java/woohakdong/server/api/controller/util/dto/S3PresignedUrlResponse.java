package woohakdong.server.api.controller.util.dto;

public record S3PresignedUrlResponse(
        String imageUrl
) {
    public static S3PresignedUrlResponse of(String presignedUrl) {
        return new S3PresignedUrlResponse(presignedUrl);
    }
}
