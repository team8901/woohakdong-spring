package woohakdong.server.common.exception;

public record ErrorResponseBody<T>(
        int detailStatusCode,
        T message
) {
}
