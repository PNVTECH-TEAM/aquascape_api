package pnvteck.auth;

import pnvteck.auth.dto.AuthRequest;
import pnvteck.auth.dto.AuthRequestLogin;

public interface AuthService {
    String register(AuthRequest request);

    String login(AuthRequestLogin request);

    String resendToken(String email);

    void logout(String token);

    boolean isTokenRevoked(String token);
}
