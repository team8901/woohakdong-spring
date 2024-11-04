package woohakdong.server.domain.schedule;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;

@RequiredArgsConstructor
@Repository
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository scheduleRepository;

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule getById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.SCHEDULE_NOT_FOUND));
    }

    @Override
    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    @Override
    public List<Schedule> getSchedulesWithPeriod(Club club, LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleRepository.findByClubAndScheduleDateTimeBetween(club, startDate, endDate);
    }
}
