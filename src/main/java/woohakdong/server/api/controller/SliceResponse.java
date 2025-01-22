package woohakdong.server.api.controller;

import java.util.List;

public record SliceResponse<T>(
        List<T> result,
        int currentPage,
        boolean hasNext
) {
    public static <T> SliceResponse<T> of(List<T> result, int currentPage, boolean hasNext) {
        return new SliceResponse<>(result, currentPage, hasNext);
    }
}
