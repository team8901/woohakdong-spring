package woohakdong.server.api.service.email;

public interface EmailClient {

    void sendEmailForClubJoin(String receiverName, String receiverEmail, String clubName, String clubDescription,
                              String groupChatLink, String clubPassword);

    void sendEmailForUpdateClubInfo(String receiverName, String receiverEmail, String clubName, String clubDescription,
                                    String groupChatLink, String clubPassword);

    void sendEmailForSchedule(String receiverName, String receiverEmail, String clubName, String scheduleTitle,
                              String scheduleContent, String scheduleDateTime);

    void sendOverdueNotification(String memberName, String memberEmail, String itemName,
                                        String clubName, String dueDate);
}
