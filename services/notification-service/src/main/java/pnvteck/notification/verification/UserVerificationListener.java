package pnvteck.notification.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pnvteck.notification.event.UserVerificationEvent;

@Component
@RequiredArgsConstructor
public class UserVerificationListener {

    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @RabbitListener(queues = "user.verification")
    public void handle(UserVerificationEvent event) {
        verificationTokenService.deleteByUserId(event.getUserId());

        VerificationToken token = verificationTokenService.createToken(event.getUserId(), event.getEmail());

        String subject = "Verify your email";
        String content = "Your OTP is: " + token.getToken() + ". It expires in 3 minutes.";

        emailService.send(event.getEmail(), subject, content);
    }
}
