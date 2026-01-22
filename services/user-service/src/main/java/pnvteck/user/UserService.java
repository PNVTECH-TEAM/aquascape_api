package pnvteck.user;

import java.util.List;
import pnvteck.user.dto.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();
}
