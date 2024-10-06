package woohakdong.server.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import woohakdong.server.domain.school.School;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String memberProvideId;
    private String memberName;
    private String memberEmail;
    private String memberPhoneNumber;
    private String memberMajor;
    private String memberStudentNumber;
    private MemberGender memberGender;

    private String memberRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    protected Member() {
        // 기본 생성자는 필드를 초기화하지 않고 비워둠
    }

    @Builder
    private Member(String memberProvideId, String memberName, String memberEmail, String memberRole, School school) {
        this.memberProvideId = memberProvideId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberRole = memberRole;
        this.school = school;
    }
}
