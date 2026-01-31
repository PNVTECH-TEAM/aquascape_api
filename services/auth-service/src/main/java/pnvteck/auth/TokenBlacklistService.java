package pnvteck.auth;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public void revoke(String token, long expiresAtMillis) {
        blacklist.put(token, expiresAtMillis);
    }

    public boolean isRevoked(String token) {
        Long expiresAt = blacklist.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
