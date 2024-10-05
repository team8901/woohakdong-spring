package woohakdong.server.domain.school;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import woohakdong.server.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schoolId;
    private String schoolName;
    private String schoolDomain;

    @JsonIgnore
    @OneToMany(mappedBy = "school")
    private List<Member> members = new ArrayList<>();
}
