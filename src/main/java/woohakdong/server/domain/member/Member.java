package woohakdong.server.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import woohakdong.server.domain.school.School;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String memberProvideId;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String memberEmail;

    private String memberPhoneNumber;
    private String memberMajor;
    private String memberStudentNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberGender memberGender;

    private String memberRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    protected Member() {}

    @Builder
    private Member(String memberProvideId, String memberName, String memberEmail, String memberRole, School school, String memberPhoneNumber, String memberMajor, String memberStudentNumber, MemberGender memberGender, String password) {
        this.memberProvideId = memberProvideId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberRole = memberRole;
        this.school = school;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberMajor = memberMajor;
        this.memberStudentNumber = memberStudentNumber;
        this.memberGender = memberGender;
        this.password = password;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public void setMemberMajor(String memberMajor) {
        this.memberMajor = memberMajor;
    }

    public void setMemberStudentNumber(String memberStudentNumber) {
        this.memberStudentNumber = memberStudentNumber;
    }

    public void setMemberGender(MemberGender memberGender) {
        this.memberGender = memberGender;
    }
}
