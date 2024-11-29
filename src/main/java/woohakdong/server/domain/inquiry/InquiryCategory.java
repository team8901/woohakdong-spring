package woohakdong.server.domain.inquiry;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InquiryCategory {
    INQUIRY("문의"),
    DECLARATION("신고"),
    SUGGESTION("제안"),
    ETC("기타");

    private final String category;
}
