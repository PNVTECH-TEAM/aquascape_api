package pnvteck.notification.verification;

import java.security.SecureRandom;
import java.util.Scanner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_GENERATE_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final VerificationTokenRepository repository;

    public VerificationToken createToken(Long userId, String email) {
        String token = generateOtp();
        for (int i = 0; i < OTP_GENERATE_ATTEMPTS; i++) {
            if (repository.findByToken(token).isEmpty()) {
                break;
            }
            token = generateOtp();
        }
        if (repository.findByToken(token).isPresent()) {
            throw new RuntimeException("Failed to generate unique OTP");
        }

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userId(userId)
                .email(email)
                .expiredAt(VerificationToken.calculateExpiry())
                .build();

        return repository.save(verificationToken);
    }

    public VerificationToken verifyToken(String email, String otp) {
        VerificationToken verificationToken = repository.findByEmailAndToken(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (verificationToken.getExpiredAt().isBefore(java.time.LocalDateTime.now())) {
            repository.delete(verificationToken);
            throw new RuntimeException("OTP expired");
        }

        return verificationToken;
    }

    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    public void deleteToken(VerificationToken token) {
        repository.delete(token);
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int value = SECURE_RANDOM.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", value);
    }
}
