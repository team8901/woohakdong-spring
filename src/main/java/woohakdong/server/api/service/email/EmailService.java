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
    public void sendEmailForGroupJoin(String memberName, String memberEmail, String clubName, String groupChatLink,
                                      String groupChatPassword) {
        emailClient.sendEmailForGroupJoin(memberEmail, clubName, groupChatLink, groupChatPassword, memberName);
    }
}
