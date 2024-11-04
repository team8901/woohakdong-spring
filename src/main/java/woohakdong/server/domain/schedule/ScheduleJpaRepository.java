package woohakdong.server.domain.schedule;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByClubAndScheduleDateTimeBetween(Club club, LocalDateTime scheduleDateTime,
                                                        LocalDateTime scheduleDateTime2);
}
