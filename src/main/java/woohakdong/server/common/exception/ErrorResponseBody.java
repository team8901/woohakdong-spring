package woohakdong.server.common.exception;

import lombok.Builder;

@Builder
public record ErrorResponseBody(
        int detailStatusCode,
        String message
) {
}
