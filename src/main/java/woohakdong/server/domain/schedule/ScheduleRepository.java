package woohakdong.server.domain.schedule;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);

    Schedule getById(Long scheduleId);
}
