package woohakdong.server.api.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import woohakdong.server.common.exception.CustomException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailClient emailClient;

    @Async
    @Retryable(retryFor = {CustomException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public void sendEmailForGroupJoin(String memberName, String memberEmail, String clubName, String clubDescription,
                                      String groupChatLink, String groupChatPassword) {
        emailClient.sendEmailForClubJoin(memberName, memberEmail, clubName, clubDescription, groupChatLink,
                groupChatPassword);
    }

    @Async
    @Retryable(retryFor = {CustomException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public void sendEmailForUpdateClubInfo(String memberName, String memberEmail, String clubName,
                                           String clubDescription, String groupChatLink, String groupChatPassword) {
        emailClient.sendEmailForUpdateClubInfo(memberName, memberEmail, clubName, clubDescription, groupChatLink,
                groupChatPassword);
    }

    @Async
    @Retryable(retryFor = {CustomException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public void sendEmailForSchedule(String memberName, String memberEmail, String clubName, String scheduleTitle,
                                     String scheduleContent, String scheduleDateTime) {
        emailClient.sendEmailForSchedule(memberName, memberEmail, clubName, scheduleTitle, scheduleContent,
                scheduleDateTime);
    }

    @Async
    @Retryable(retryFor = {CustomException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public void sendOverdueNotification(String memberName, String memberEmail, String itemName,
                                        String clubName, String dueDate) {
        emailClient.sendOverdueNotification(memberName, memberEmail, itemName, clubName, dueDate);
    }
}
