package pnvteck.notification.verification;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByEmailAndToken(String email, String token);

    List<VerificationToken> findByExpiredAtBefore(LocalDateTime time);

    void deleteByUserId(Long userId);
}
