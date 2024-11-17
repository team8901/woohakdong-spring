package woohakdong.server.api.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.schedule.dto.ScheduleCreateRequest;
import woohakdong.server.api.controller.schedule.dto.ScheduleIdResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleInfoResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleUpdateRequest;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.schedule.Schedule;
import woohakdong.server.domain.schedule.ScheduleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClubRepository clubRepository;

    @Transactional
    public ScheduleIdResponse createSchedule(Long clubId, ScheduleCreateRequest request) {
        Club club = clubRepository.getById(clubId);

        Schedule schedule = Schedule.create(request.scheduleTitle(), request.scheduleContent(),
                request.scheduleDateTime(), request.scheduleColor(), club);
        scheduleRepository.save(schedule);

        return ScheduleIdResponse.from(schedule);
    }

    public ScheduleInfoResponse getSchedule(Long clubId, Long scheduleId) {
        clubRepository.validateClubExists(clubId);

        Schedule schedule = scheduleRepository.getById(scheduleId);

        return ScheduleInfoResponse.from(schedule);
    }

    @Transactional
    public void deleteSchedule(Long clubId, Long scheduleId) {
        clubRepository.validateClubExists(clubId);

        Schedule schedule = scheduleRepository.getById(scheduleId);

        scheduleRepository.delete(schedule);
    }

    @Transactional
    public ScheduleIdResponse updateSchedule(Long clubId, Long scheduleId, ScheduleUpdateRequest request) {
        clubRepository.validateClubExists(clubId);
        Schedule schedule = scheduleRepository.getById(scheduleId);

        schedule.update(request.scheduleTitle(), request.scheduleContent(),
                request.scheduleDateTime(), request.scheduleColor());

        return ScheduleIdResponse.from(schedule);
    }

    public List<ScheduleInfoResponse> getSchedules(Long clubId, LocalDate date) {
        Club club = clubRepository.getById(clubId);

        // 저번 달 마지막 날부터 다음 달 첫 날까지의 스케줄을 가져온다. ( 프론트 달력 이벤트 이슈로 인해 )
        LocalDateTime startDate = LocalDate.of(date.getYear(), date.getMonth(), 1).atStartOfDay().minusDays(1);
        LocalDateTime endDate = startDate.plusMonths(1).plusDays(1).withHour(23).withMinute(59).withSecond(59);

        return scheduleRepository.getSchedulesWithPeriod(club, startDate, endDate)
                .stream()
                .map(ScheduleInfoResponse::from)
                .toList();
    }
}
