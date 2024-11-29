package woohakdong.server.domain.inquiry;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.member.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @Column(nullable = false)
    private String inquiryContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryCategory inquiryCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Inquiry(String InquiryContent, InquiryCategory inquiryCategory, Member member) {
        this.inquiryContent = InquiryContent;
        this.inquiryCategory = inquiryCategory;
        this.member = member;
    }

    static public Inquiry create(String InquiryContent, InquiryCategory inquiryCategory, Member member) {
        return Inquiry.builder()
                .InquiryContent(InquiryContent)
                .inquiryCategory(inquiryCategory)
                .member(member)
                .build();
    }
}
