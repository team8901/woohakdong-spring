package woohakdong.server.api.service.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.schedule.dto.ScheduleCreateRequest;
import woohakdong.server.api.controller.schedule.dto.ScheduleIdResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleInfoResponse;
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
    public ScheduleIdResponse createSchedule(Long clubId, ScheduleCreateRequest scheduleCreateRequest) {
        Club club = clubRepository.getById(clubId);
        Schedule schedule = Schedule.create(scheduleCreateRequest, club);
        scheduleRepository.save(schedule);

        return ScheduleIdResponse.from(schedule);
    }

    public ScheduleInfoResponse getSchedule(Long clubId, Long scheduleId) {
        clubRepository.validateClubExists(clubId);
        Schedule schedule = scheduleRepository.getById(scheduleId);
        return ScheduleInfoResponse.from(schedule);
    }
}
