package woohakdong.server.domain.inquiry;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InquiryCategory {
    INQUIRY("inquiry"),
    DECLARATION("declaration"),
    SUGGESTION("suggestion"),
    ETC("etc");

    private final String category;
}
