package woohakdong.server.domain.clubmember;

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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubMemberId;

    private LocalDate clubJoinedDate;

    @Column(nullable = false)
    private LocalDate clubMemberAssignedTerm;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClubMemberRole clubMemberRole;

    private String clubGeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Builder
    private ClubMember(Club club, LocalDate clubJoinedDate, LocalDate clubMemberAssignedTerm,
                       ClubMemberRole clubMemberRole, Member member) {
        this.club = club;
        this.clubJoinedDate = clubJoinedDate;
        this.clubMemberAssignedTerm = clubMemberAssignedTerm;
        this.clubMemberRole = clubMemberRole;
        this.member = member;
    }

    public static ClubMember create(Club club, LocalDate assignedTerm, ClubMemberRole clubMemberRole, Member member) {
        return ClubMember.builder()
                .club(club)
                .clubJoinedDate(LocalDate.now())
                .clubMemberAssignedTerm(assignedTerm)
                .clubMemberRole(clubMemberRole)
                .member(member)
                .build();
    }


    public void changeRole(ClubMemberRole clubMemberRole) {
        this.clubMemberRole = clubMemberRole;
    }
}
