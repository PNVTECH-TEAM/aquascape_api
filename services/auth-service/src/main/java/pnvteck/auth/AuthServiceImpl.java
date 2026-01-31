package pnvteck.auth;

import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pnvteck.auth.client.UserServiceClient;
import pnvteck.auth.client.dto.CreateUserRequest;
import pnvteck.auth.client.dto.UserInternalResponse;
import pnvteck.auth.dto.AuthRequest;
import pnvteck.auth.dto.AuthRequestLogin;
import pnvteck.auth.event.UserVerificationEvent;
import pnvteck.common.constant.AccountStatus;
import pnvteck.common.constant.Role;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[0-9])(?=.*[A-Za-z]).{6,}$");

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RabbitTemplate rabbitTemplate;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public String register(AuthRequest request) {
        if (!EMAIL_REGEX.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Email invalid");
        }

        if (!PASSWORD_REGEX.matcher(request.getPassword()).matches()) {
            throw new RuntimeException("Password must be at least 6 chars and contain letters and digits");
        }

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(AccountStatus.INACTIVE)
                .build();

        UserInternalResponse created = userServiceClient.createUser(createUserRequest);

        publishVerificationEvent(created, "REGISTER");

        return "Register success. Please check email for OTP.";
    }

    @Override
    public String login(AuthRequestLogin request) {
        if (!EMAIL_REGEX.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Email invalid");
        }

        UserInternalResponse user = userServiceClient.getByEmail(request.getEmail());

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account not verified");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Password invalid");
        }

        return jwtProvider.generateToken(user.getUsername());
    }

    @Override
    public String resendToken(String email) {
        UserInternalResponse user = userServiceClient.getByEmail(email);
        publishVerificationEvent(user, "RESEND");
        return "Verification OTP sent";
    }

    @Override
    public void logout(String token) {
        long expiresAt = jwtProvider.getExpirationMillis(token);
        tokenBlacklistService.revoke(token, expiresAt);
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return tokenBlacklistService.isRevoked(token);
    }

    private void publishVerificationEvent(UserInternalResponse user, String eventType) {
        UserVerificationEvent event = UserVerificationEvent.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .eventType(eventType)
                .build();

        rabbitTemplate.convertAndSend("user.verification", event);
    }
}
