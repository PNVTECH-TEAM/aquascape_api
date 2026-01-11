package pnvteck.all_in_one.verification;

import lombok.RequiredArgsConstructor;
import pnvteck.all_in_one.user.UserEntity;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository repository;

    public VerificationToken createToken(UserEntity user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiredAt(VerificationToken.calculateExpiry())
                .build();

        return repository.save(verificationToken);
    }

    public UserEntity verifyToken(String token) {

        VerificationToken verificationToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiredAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        return verificationToken.getUser();
    }
}
