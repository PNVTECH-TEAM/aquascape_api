package pnvteck.all_in_one.auth;

import pnvteck.all_in_one.auth.dto.AuthRequest;
import pnvteck.all_in_one.auth.dto.AuthRequestLogin;

public interface AuthService {
    String register(AuthRequest request);
    String login(AuthRequestLogin request);
}
