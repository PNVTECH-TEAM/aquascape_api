package pnvteck.notification.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pnvteck.notification.event.UserVerificationEvent;

@Component
@RequiredArgsConstructor
public class UserVerificationListener {

    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Value("${verify.url-base}")
    private String verifyUrlBase;

    @RabbitListener(queues = "user.verification")
    public void handle(UserVerificationEvent event) {
        verificationTokenService.deleteByUserId(event.getUserId());

        VerificationToken token = verificationTokenService.createToken(event.getUserId(), event.getEmail());
        String verifyUrl = verifyUrlBase + "?token=" + token.getToken();

        String subject = "Verify your email";
        String content = "Click the link to verify: " + verifyUrl;

        emailService.send(event.getEmail(), subject, content);
    }
}
