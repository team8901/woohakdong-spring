package woohakdong.server.domain.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;

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
}
