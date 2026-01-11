package pnvteck.all_in_one.verification;

import jakarta.persistence.*;
import lombok.*;
import pnvteck.all_in_one.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    private static final int EXPIRATION_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public static LocalDateTime calculateExpiry() {
        return LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }
}
