package woohakdong.server.domain.schedule;

import java.time.LocalDateTime;
import java.util.List;
import woohakdong.server.domain.club.Club;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);

    Schedule getById(Long scheduleId);

    void delete(Schedule schedule);

    List<Schedule> getSchedulesWithPeriod(Club club, LocalDateTime startDate, LocalDateTime endDate);
}
