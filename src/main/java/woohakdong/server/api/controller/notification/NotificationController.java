package woohakdong.server.api.controller.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clubs/{clubId}/notifications")
public class NotificationController implements NotificationControllerDocs {

    @PostMapping("/clubs")
    public void sendNotificationWithClub(@PathVariable Long clubId) {

    }

    @PostMapping("/schedules/{scheduleId}")
    public void sendNotificationWithSchedule(@PathVariable Long clubId,
                                             @PathVariable Long scheduleId) {

    }

    @PostMapping("/groups/{groupId}")
    public void sendNotificationWithGroup(@PathVariable Long clubId,
                                          @PathVariable Long groupId) {

    }
}
