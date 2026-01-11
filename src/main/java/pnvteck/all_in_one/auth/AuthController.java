package pnvteck.all_in_one.auth;

import lombok.RequiredArgsConstructor;
import pnvteck.all_in_one.auth.dto.AuthRequestLogin;
import pnvteck.all_in_one.user.UserEntity;
import pnvteck.all_in_one.user.UserRepository;
import pnvteck.all_in_one.verification.EmailService;
import pnvteck.all_in_one.verification.VerificationToken;
import pnvteck.all_in_one.verification.VerificationTokenRepository;
import pnvteck.all_in_one.verification.VerificationTokenService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationTokenService verificationTokenService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @PostMapping("/register")
    public String register(@RequestBody pnvteck.all_in_one.auth.dto.AuthRequest request) {
        return authService.register(request);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {

        UserEntity user = verificationTokenService.verifyToken(token);

        user.setStatus(pnvteck.all_in_one.common.constant.AccountStatus.ACTIVE);
        userRepository.save(user);

        return "Email verified successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequestLogin request) {
        return authService.login(request);
    }

    @GetMapping("/resend-token")
    public String resendToken(@RequestParam String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Xóa token cũ
        verificationTokenRepository.deleteByUserId(user.getId());

        // Tạo token mới
        VerificationToken newToken = verificationTokenService.createToken(user);

        String verifyUrl = "http://localhost:5173/verify-email?token=" + newToken.getToken();

        emailService.send(
                email,
                "Resend verify token",
                "Click link verify mới: " + verifyUrl);

        return "Đã gửi lại email verify.";
    }

}
