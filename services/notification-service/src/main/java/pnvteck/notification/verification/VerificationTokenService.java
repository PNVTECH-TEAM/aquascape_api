package pnvteck.notification.verification;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository repository;

    public VerificationToken createToken(Long userId, String email) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userId(userId)
                .email(email)
                .expiredAt(VerificationToken.calculateExpiry())
                .build();

        return repository.save(verificationToken);
    }

    public VerificationToken verifyToken(String token) {
        VerificationToken verificationToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiredAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        return verificationToken;
    }

    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    public void deleteToken(VerificationToken token) {
        repository.delete(token);
    }
}
