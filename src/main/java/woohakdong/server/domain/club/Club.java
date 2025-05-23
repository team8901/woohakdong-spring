package woohakdong.server.domain.club;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.schedule.Schedule;
import woohakdong.server.domain.school.School;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Club extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubId;

    @Column(nullable = false)
    private String clubName;

    @Column(nullable = false)
    private String clubEnglishName;

    private String clubDescription;

    private String clubImage;

    private String clubRoom;

    private String clubGeneration;

    @Column(nullable = false)
    private String clubGroupChatLink;

    private String clubGroupChatPassword;

    private Integer clubDues;

    @Column(nullable = false)
    private LocalDate clubExpirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<ClubMember> clubMembers = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<ClubHistory> clubHistorys = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    @Builder
    private Club(String clubDescription, String clubEnglishName, String clubImage, String clubName, String clubRoom,
                 String clubGeneration, Integer clubDues, String clubGroupChatLink, String clubGroupChatPassword,
                 LocalDate clubExpirationDate, School school) {
        this.clubDescription = clubDescription;
        this.clubEnglishName = clubEnglishName;
        this.clubImage = clubImage;
        this.clubName = clubName;
        this.clubRoom = clubRoom;
        this.clubGeneration = clubGeneration;
        this.clubDues = clubDues;
        this.clubGroupChatLink = clubGroupChatLink;
        this.clubGroupChatPassword = clubGroupChatPassword;
        this.school = school;
        this.clubExpirationDate = clubExpirationDate;
    }

    public static Club create(String clubName, String clubEnglishName, String clubDescription, String clubImage,
                              String clubRoom, String clubGeneration, Integer clubDues, String clubGroupChatLink,
                              String clubGroupChatPassword, LocalDate date, School school) {
        return Club.builder()
                .clubDescription(clubDescription)
                .clubEnglishName(clubEnglishName)
                .clubImage(clubImage)
                .clubName(clubName)
                .clubRoom(clubRoom)
                .clubGeneration(clubGeneration)
                .clubDues(clubDues)
                .clubGroupChatLink(clubGroupChatLink)
                .clubGroupChatPassword(clubGroupChatPassword)
                .school(school)
                .clubExpirationDate(date)
                .build();
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public void addClubMember(ClubMember clubMember) {
        this.clubMembers.add(clubMember);
    }

    public void addClubHistory(ClubHistory clubHistory) {
        this.clubHistorys.add(clubHistory);
    }

    public void update(String clubImage, String clubDescription, String clubRoom, String clubGeneration,
                       String clubGroupChatLink, String clubGroupChatPassword, Integer clubDues) {
        this.clubImage = clubImage;
        this.clubDescription = clubDescription;
        this.clubRoom = clubRoom;
        this.clubGeneration = clubGeneration;
        this.clubDues = clubDues;
        this.clubGroupChatLink = clubGroupChatLink;
        this.clubGroupChatPassword = clubGroupChatPassword;
    }

    public boolean isExpired(LocalDate date) {
        return date.isAfter(clubExpirationDate);
    }

    public void extendClubExpirationDate() {
        this.clubExpirationDate = clubExpirationDate.plusMonths(6);
    }
}
