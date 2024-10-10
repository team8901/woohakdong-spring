package woohakdong.server.domain.club;

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
import woohakdong.server.domain.gathering.Gathering;
import woohakdong.server.domain.school.School;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubId;

    @Column(nullable = false)
    private String clubName;

    @Column(nullable = false)
    private String clubEnglishName;

    private String clubDescription;
    private String clubImage;
    private LocalDate clubEstablishmentDate;
    private String clubRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "club")
    private List<Gathering> gatherings = new ArrayList<>();

    @Builder
    public Club(String clubDescription, String clubEnglishName, LocalDate clubEstablishmentDate, String clubImage,
                String clubName, String clubRoom, School school) {
        this.clubDescription = clubDescription;
        this.clubEnglishName = clubEnglishName;
        this.clubEstablishmentDate = clubEstablishmentDate;
        this.clubImage = clubImage;
        this.clubName = clubName;
        this.clubRoom = clubRoom;
        this.school = school;
    }
}
