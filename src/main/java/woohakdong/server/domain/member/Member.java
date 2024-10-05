package woohakdong.server.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long MemberId;
    private String MemberProvideId;
    private String MemberName;
    private String MemberEmail;
    private String MemberPhoneNumber;
    private String MemberMajor;
    private String MemberStudentNumber;
    private String MemberGender;

    private String MemberRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;
}
