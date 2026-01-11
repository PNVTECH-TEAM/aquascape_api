package pnvteck.all_in_one.user;

import java.util.List;

import pnvteck.all_in_one.user.dto.UserResponse;
public interface UserService {
    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();
}