package woohakdong.server.api.service.email;

public interface EmailClient {

    void sendEmailForGroupJoin(String receiverEmail, String clubName, String groupChatLink,
                               String clubPassword, String receiverName);
}
