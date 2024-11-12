package woohakdong.server.domain.school;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class School extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schoolId;
    private String schoolName;
    private String schoolDomain;

    @JsonIgnore
    @OneToMany(mappedBy = "school")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "school")
    private List<Club> clubs = new ArrayList<>();

    @Builder
    public School(String schoolName, String schoolDomain) {
        this.schoolName = schoolName;
        this.schoolDomain = schoolDomain;
    }
}
