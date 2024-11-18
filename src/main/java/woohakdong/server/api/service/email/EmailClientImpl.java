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

    private final static String CLUB_INVITE_EMAIL_SUBJECT = "%s 동아리 초대장입니다.";
    private final static String CLUB_INFO_CHANGED_EMAIL_SUBJECT = "%s 동아리 정보가 변경되었습니다.";
    private final static String SCHEDULE_NOTICE_EMAIL_SUBJECT = "%s 동아리에 새로운 일정이 등록되었습니다!";
    private final static String ITEM_OVERDUE_NOTICE_EMAIL_SUBJECT = "%s 동아리 물품 반납 연체 알림입니다.";

    private final JavaMailSender mailSender;

    @Override
    public void sendEmailForClubJoin(String receiverName, String receiverEmail, String clubName,
                                     String clubDescription, String groupChatLink, String groupChatPassword) {
        String htmlText = createClubJoinHtml(clubName, clubDescription, receiverName, groupChatPassword, groupChatLink);
        sendEmail(htmlText, receiverEmail, CLUB_INVITE_EMAIL_SUBJECT.formatted(clubName));
    }

    @Override
    public void sendEmailForUpdateClubInfo(String receiverName, String receiverEmail, String clubName,
                                           String clubDescription, String groupChatLink, String clubPassword) {
        String htmlText = createClubUpdateHtml(clubName, clubDescription, receiverName, clubPassword, groupChatLink);
        sendEmail(htmlText, receiverEmail, CLUB_INFO_CHANGED_EMAIL_SUBJECT.formatted(clubName));
    }

    @Override
    public void sendEmailForSchedule(String receiverName, String receiverEmail, String clubName, String scheduleTitle,
                                     String scheduleContent, String scheduleDate) {
        String htmlText = createScheduleInfoHtml(clubName, receiverName, scheduleTitle, scheduleContent, scheduleDate);
        sendEmail(htmlText, receiverEmail, SCHEDULE_NOTICE_EMAIL_SUBJECT.formatted(clubName));
    }

    @Override
    public void sendOverdueNotification(String memberName, String receiverEmail, String itemName,
                                        String clubName, String dueDate) {
        String htmlText = createItemOverdueHtml(memberName, itemName, clubName, dueDate);
        sendEmail(htmlText, receiverEmail, ITEM_OVERDUE_NOTICE_EMAIL_SUBJECT.formatted(clubName));
    }

    private void sendEmail(String htmlText, String receiverEmail, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(receiverEmail);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(MAIL_SEND_ERROR);
        }
    }

    private String createClubJoinHtml(String clubName, String clubDescription, String receiverName, String clubPassword,
                                      String groupChatLink) {
        return """
                <html>
                           <body style="font-family: Arial, sans-serif; color: #333; background-color: #ffffff;">
                               <div style="background-color: #ffffff; padding: 20px; border-radius: 5px; max-width: 600px; margin: 0 auto; border: 1px solid #b3e0ff;">
                                   <h3 style="color: #3399ff;">%s 동아리 초대 링크입니다.</h3>

                                   <p>안녕하세요 <strong>%s</strong>님. 새로운 동아리에 초대되었어요!</p>

                                   <div style="background-color: #f7faff; padding: 15px; border-radius: 5px; margin-top: 10px;">
                                       <p><strong>동아리 이름:</strong> %s</p>
                                       <p><strong>동아리 소개:</strong> %s</p>
                                       <p><strong>채팅방 비밀번호:</strong> %s</p>
                                   </div>

                                   <p>아래의 버튼을 클릭하여 동아리 채팅방에 접속해보세요!</p>

                                   <div style="text-align: center; margin: 20px 0;">
                                       <a href="%s" style="display: inline-block; background-color: #3399ff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">동아리 채팅방 참여하기</a>
                                   </div>
                               </div>

                               <footer style="margin-top: 20px; text-align: center; font-size: 12px; color: #555;">
                                   <p>Contact us at: <a href="mailto:support@8901.dev@gmail.com" style="color: #3399ff;">8901.dev@gmail.com</a></p>
                                   <p>&copy; 2024 Woohakdong. All rights reserved.</p>
                               </footer>
                           </body>
                       </html>
                """.formatted(clubName, receiverName, clubName, clubDescription, clubPassword, groupChatLink);
    }

    private String createClubUpdateHtml(String clubName, String clubDescription, String receiverName,
                                        String clubPassword, String groupChatLink) {
        return """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333; background-color: #ffffff;">
                        <div style="background-color: #ffffff; padding: 20px; border-radius: 5px; max-width: 600px; margin: 0 auto; border: 1px solid #b3e0ff;">
                            <h3 style="color: #3399ff;">%s 동아리의 정보 변경사항입니다.</h3>

                            <p>안녕하세요 <strong>%s</strong>님. 동아리의 변경된 정보는 다음과 같아요!</p>

                                <div style="background-color: #f7faff; padding: 15px; border-radius: 5px; margin-top: 10px;">
                                    <p><strong>동아리 이름:</strong> %s</p>
                                    <p><strong>동아리 소개:</strong> %s</p>
                                    <p><strong>채팅방 비밀번호:</strong> %s</p>
                                </div>

                            <p>아래의 버튼을 클릭하여 변경된 동아리 채팅방에 접속해보세요!</p>

                            <div style="text-align: center; margin: 20px 0;">
                                <a href="%s" style="display: inline-block; background-color: #3399ff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">동아리 채팅방</a>
                            </div>
                        </div>

                        <footer style="margin-top: 20px; text-align: center; font-size: 12px; color: #555;">
                            <p>Contact us at: <a href="mailto:support@8901.dev@gmail.com" style="color: #3399ff;">8901.dev@gmail.com</a></p>
                            <p>&copy; 2024 Woohakdong. All rights reserved.</p>
                        </footer>
                    </body>
                </html>
                """.formatted(clubName, receiverName, clubName, clubDescription, clubPassword, groupChatLink);
    }

    private String createScheduleInfoHtml(String clubName, String receiverName, String scheduleTitle,
                                          String scheduleContent, String scheduleDateTime) {
        return """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333; background-color: #ffffff;">
                        <div style="background-color: #ffffff; padding: 20px; border-radius: 5px; max-width: 600px; margin: 0 auto; border: 1px solid #b3e0ff;">
                            <h3 style="color: #3399ff;">%s 동아리에 새로운 일정이 등록되었습니다!</h3>

                            <p>안녕하세요 <strong>%s</strong>님. %s 동아리에 새로운 일정이 추가되었어요!</p>

                            <div style="background-color: #f7faff; padding: 15px; border-radius: 5px; margin-top: 10px;">
                                <p><strong>일정 제목:</strong> %s</p>
                                <p><strong>일정 내용:</strong> %s</p>
                                <p><strong>일정 시간:</strong> %s</p>
                            </div>
                        </div>

                        <footer style="margin-top: 20px; text-align: center; font-size: 12px; color: #555;">
                            <p>Contact us at: <a href="mailto:support@8901.dev@gmail.com" style="color: #3399ff;">8901.dev@gmail.com</a></p>
                            <p>&copy; 2024 Woohakdong. All rights reserved.</p>
                        </footer>
                    </body>
                </html>
                """.formatted(clubName, receiverName, clubName, scheduleTitle, scheduleContent, scheduleDateTime);
    }

    private String createItemOverdueHtml(String memberName, String itemName,
                                         String clubName, String dueDate) {
        return """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333; background-color: #ffffff;">
                        <div style="background-color: #ffffff; padding: 20px; border-radius: 5px; max-width: 600px; margin: 0 auto; border: 1px solid #b3e0ff;">
                            <h3 style="color: #3399ff;">%s 동아리의 물품 연체 알림</h3>

                            <p>안녕하세요 <strong>%s</strong>님,</p>
                            <p>다음 물품이 연체되었습니다:</p>

                            <div style="background-color: #f7faff; padding: 15px; border-radius: 5px; margin-top: 10px;">
                                <p><strong>동아리 이름:</strong> %s</p>
                                <p><strong>물품 이름:</strong> %s</p>
                                <p><strong>반납 예정일:</strong> %s</p>
                            </div>
                            
                            <p>가능한 한 빨리 물품을 반납해주시길 부탁드립니다.</p>
                            
                        </div>

                        <footer style="margin-top: 20px; text-align: center; font-size: 12px; color: #555;">
                            <p>Contact us at: <a href="mailto:support@8901.dev@gmail.com" style="color: #3399ff;">8901.dev@gmail.com</a></p>
                            <p>&copy; 2024 Woohakdong. All rights reserved.</p>
                        </footer>
                    </body>
                </html>
                """.formatted(clubName, memberName, clubName, itemName, dueDate);
    }
}
