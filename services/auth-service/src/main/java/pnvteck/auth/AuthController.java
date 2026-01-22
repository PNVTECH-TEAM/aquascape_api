package pnvteck.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pnvteck.auth.dto.AuthRequest;
import pnvteck.auth.dto.AuthRequestLogin;
import pnvteck.auth.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequestLogin request) {
        String token = authService.login(request);
        return AuthResponse.builder().token(token).build();
    }

    @GetMapping("/resend-token")
    public String resendToken(@RequestParam String email) {
        return authService.resendToken(email);
    }
}
