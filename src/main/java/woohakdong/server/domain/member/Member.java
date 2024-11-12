package woohakdong.server.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.school.School;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

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
    private String memberPassword;

    @Enumerated(EnumType.STRING)
    private MemberGender memberGender;

    private String memberRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @Builder
    private Member(String memberProvideId, String memberName, String memberEmail, String memberRole, School school,
                   String memberPhoneNumber, String memberMajor, String memberStudentNumber, MemberGender memberGender,
                   String memberPassword) {
        this.memberProvideId = memberProvideId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberRole = memberRole;
        this.school = school;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberMajor = memberMajor;
        this.memberStudentNumber = memberStudentNumber;
        this.memberGender = memberGender;
        this.memberPassword = memberPassword;
    }

    public static Member createAdmin(String loginId, String memberName, String memberEmail, String memberPassword) {
        return Member.builder()
                .memberProvideId(loginId)
                .memberName(memberName)
                .memberRole("ADMIN_ROLE")
                .memberEmail(memberEmail)
                .memberPassword(memberPassword)
                .build();
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

    public void adminUpdate(String memberProvideId, String memberName, String memberEmail, String memberPassword) {
        this.memberProvideId = memberProvideId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
    }
}
