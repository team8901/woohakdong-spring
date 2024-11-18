package woohakdong.server.api.service.item;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import woohakdong.server.api.service.notification.NotificationService;

@Component
@RequiredArgsConstructor
public class ItemOverdueNotificationService {
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 17 * * ?")
    public void scheduleOverdueNotification() {
        notificationService.notifyOverdueItems();
    }
}
