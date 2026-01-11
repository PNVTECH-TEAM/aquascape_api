package pnvteck.all_in_one.verification;

public interface EmailService {
    void send(String to, String subject, String content);
}