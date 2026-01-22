package pnvteck.notification.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pnvteck.common.constant.AccountStatus;
import pnvteck.notification.client.UserServiceClient;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationTokenService verificationTokenService;
    private final UserServiceClient userServiceClient;

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        VerificationToken verificationToken = verificationTokenService.verifyToken(token);
        userServiceClient.updateStatus(verificationToken.getUserId(), AccountStatus.ACTIVE.name());
        verificationTokenService.deleteToken(verificationToken);
        return "Email verified successfully";
    }
}
