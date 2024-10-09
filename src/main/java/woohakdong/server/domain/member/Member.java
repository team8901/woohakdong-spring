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

    @Enumerated(EnumType.STRING)
    private MemberGender memberGender;

    private String memberRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    protected Member() {}

    @Builder
    private Member(String memberProvideId, String memberName, String memberEmail, String memberRole, School school, String memberPhoneNumber, String memberMajor, String memberStudentNumber, MemberGender memberGender) {
        this.memberProvideId = memberProvideId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberRole = memberRole;
        this.school = school;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberMajor = memberMajor;
        this.memberStudentNumber = memberStudentNumber;
        this.memberGender = memberGender;
    }
}
