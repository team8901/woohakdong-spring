package woohakdong.server.domain.schedule;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.api.controller.schedule.dto.ScheduleCreateRequest;
import woohakdong.server.domain.club.Club;

@Entity
@Getter
@Table(name = "\"schedule\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(nullable = false)
    private String scheduleTitle;

    private String scheduleContent;

    private LocalDateTime scheduleDateTime;

    private String scheduleColor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "club_id")
    private Club club;

    @Builder
    private Schedule(String scheduleTitle, String scheduleContent, LocalDateTime scheduleDateTime, String scheduleColor,
                     Club club) {
        this.scheduleContent = scheduleContent;
        this.scheduleDateTime = scheduleDateTime;
        this.scheduleTitle = scheduleTitle;
        this.scheduleColor = scheduleColor;
        this.club = club;
    }

    public static Schedule create(ScheduleCreateRequest scheduleCreateRequest, Club club) {
        return Schedule.builder()
                .scheduleTitle(scheduleCreateRequest.scheduleTitle())
                .scheduleContent(scheduleCreateRequest.scheduleContent())
                .scheduleDateTime(scheduleCreateRequest.scheduleDateTime())
                .scheduleColor(scheduleCreateRequest.scheduleColor())
                .club(club)
                .build();
    }
}
