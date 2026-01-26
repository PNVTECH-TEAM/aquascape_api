package pnvteck.notification.verification;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pnvteck.notification.client.UserServiceClient;

@Component
@RequiredArgsConstructor
public class ExpiredVerificationCleanup {

    private final VerificationTokenRepository repository;
    private final UserServiceClient userServiceClient;

    @Scheduled(fixedDelayString = "${verify.cleanup-interval-ms:60000}")
    public void cleanupExpiredTokens() {
        List<VerificationToken> expiredTokens = repository.findByExpiredAtBefore(LocalDateTime.now());
        for (VerificationToken token : expiredTokens) {
            userServiceClient.deleteIfInactive(token.getUserId());
            repository.delete(token);
        }
    }
}
