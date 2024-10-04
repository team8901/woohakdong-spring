package woohakdong.server.common.exception;

public record ErrorResponseBody(
        int detailStatusCode,
        String message
) {
}
