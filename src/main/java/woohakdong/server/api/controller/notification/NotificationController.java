package woohakdong.server.api.controller.notification;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.service.notification.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clubs/{clubId}/notifications")
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @PostMapping("/clubs")
    public void sendNotificationWithClubInfoUpdate(@PathVariable Long clubId) {
        notificationService.sendNotificationWithClubInfoUpdate(clubId, getAssignedTerm());
    }

    @PostMapping("/schedules/{scheduleId}")
    public void sendNotificationWithSchedule(@PathVariable Long clubId,
                                             @PathVariable Long scheduleId) {
        notificationService.sendNotificationWithSchedule(clubId, scheduleId, getAssignedTerm());
    }

    @PostMapping("/groups/{groupId}")
    public void sendNotificationWithGroup(@PathVariable Long clubId,
                                          @PathVariable Long groupId) {

    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}
