package pnvteck.all_in_one.auth;

import lombok.RequiredArgsConstructor;
import pnvteck.all_in_one.auth.dto.AuthRequest;
import pnvteck.all_in_one.auth.dto.AuthRequestLogin;
import pnvteck.all_in_one.common.constant.AccountStatus;
import pnvteck.all_in_one.common.constant.Role;
import pnvteck.all_in_one.user.UserEntity;
import pnvteck.all_in_one.user.UserRepository;
import pnvteck.all_in_one.verification.EmailService;
import pnvteck.all_in_one.verification.VerificationToken;
import pnvteck.all_in_one.verification.VerificationTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[0-9])(?=.*[A-Za-z]).{6,}$");

    @Override
    public String register(AuthRequest request) {

        if (!EMAIL_REGEX.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Email invalid");
        }

        if (!PASSWORD_REGEX.matcher(request.getPassword()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password >= 6 ký tự, chứa chữ và số");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email exists");
        }

        // Create user
        UserEntity user = UserEntity.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(AccountStatus.INACTIVE) // CHƯA ACTIVE
                .build();

        userRepository.save(user);

        // Create email token
        VerificationToken token = verificationTokenService.createToken(user);

        String verifyUrl = "http://localhost:5173/verify-email?token=" + token.getToken();

        emailService.send(
                user.getEmail(),
                "Verify your email",
                "Click link để verify tài khoản: " + verifyUrl);

        return "Register thành công! Hãy kiểm tra email để verify.";
    }

    @Override
    public String login(AuthRequestLogin request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account chưa verify email!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password sai");
        }

        return jwtProvider.generateToken(user.getUsername());
    }

}
