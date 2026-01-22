package pnvteck.notification.verification;

public interface EmailService {
    void send(String to, String subject, String content);
}
