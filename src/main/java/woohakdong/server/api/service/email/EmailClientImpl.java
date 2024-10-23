package woohakdong.server.api.service.email;

import static woohakdong.server.common.exception.CustomErrorInfo.MAIL_SEND_ERROR;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import woohakdong.server.common.exception.CustomException;

@Component
@RequiredArgsConstructor
public class EmailClientImpl implements EmailClient {

    private final JavaMailSender mailSender;
    private final String CLUB_INVITE_EMAIL_SUBJECT = "%s 동아리 초대장입니다.";

    @Override
    public void sendEmailForGroupJoin(String receiverEmail, String clubName, String groupChatLink,
                                      String clubPassword, String receiverName) {
        try {
            String htmlText = """
                    <html>
                        <body style="font-family: Arial, sans-serif; color: #333; background-color: #ffffff;">
                            <div style="background-color: #ffffff; padding: 20px; border-radius: 5px; max-width: 600px; margin: 0 auto; border: 1px solid #b3e0ff;">
                                <h3 style="color: #3399ff;">%s 동아리 초대 링크입니다.</h3>
                                <p>안녕하세요 %s님!. %s 동아리에 초대되셨습니다.</p>
                                <p>아래의 링크를 클릭하여 동아리에서 잊지 못할 대학 생활을 만들어보세요!</p>
                                <p>비밀번호: %s</p>
                                <div style="text-align: center; margin: 20px 0;">
                                    <a href="%s" style="display: inline-block; background-color: #3399ff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">동아리 참여 링크</a>
                                </div>
                                <p style="text-align: right;">감사합니다.<br/>8901 드림</p>
                            </div>

                            <footer style="margin-top: 20px; text-align: center; font-size: 12px; color: #555;">
                                <p>Contact us at: <a href="mailto:support@8901.dev@gmail.com" style="color: #3399ff;">8901.dev@gmail.com</a></p>
                                <p>&copy; 2024 Woohakdong. All rights reserved.</p>
                            </footer>

                        </body>
                    </html>
                    """.formatted(clubName, receiverName, clubName, clubPassword, groupChatLink);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(receiverEmail);
            helper.setSubject(CLUB_INVITE_EMAIL_SUBJECT.formatted(clubName));
            helper.setText(htmlText, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(MAIL_SEND_ERROR);
        }
    }
}
