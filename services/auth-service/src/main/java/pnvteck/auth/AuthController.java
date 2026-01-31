package pnvteck.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pnvteck.auth.dto.AuthRequest;
import pnvteck.auth.dto.AuthRequestLogin;
import pnvteck.auth.dto.AuthResponse;
import pnvteck.auth.dto.TokenStatusResponse;
import pnvteck.common.response.ApiResponse;

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

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                authService.logout(token);
            }
            return ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Logout success")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .code(1001)
                    .message("Logout failed: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/token-status")
    public TokenStatusResponse tokenStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = extractToken(authHeader);
        boolean revoked = authService.isTokenRevoked(token);
        return TokenStatusResponse.builder().revoked(revoked).build();
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        return authHeader.substring("Bearer ".length());
    }
}
