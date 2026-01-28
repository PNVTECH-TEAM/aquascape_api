package pnvteck.user;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pnvteck.common.constant.Role;
import pnvteck.common.exception.CustomException;
import pnvteck.user.dto.UserResponse;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserById(Long id, String requesterUsername) {
        UserEntity requester = getRequester(requesterUsername);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (requester.getRole() != Role.ADMIN && !user.getUsername().equals(requester.getUsername())) {
            throw new CustomException(1003, "Forbidden");
        }

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers(String requesterUsername) {
        UserEntity requester = getRequester(requesterUsername);
        if (requester.getRole() != Role.ADMIN) {
            throw new CustomException(1003, "Forbidden");
        }
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserResponse.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .build())
                .collect(Collectors.toList());
    }

    private UserEntity getRequester(String requesterUsername) {
        if (requesterUsername == null || requesterUsername.isBlank()) {
            throw new CustomException(1002, "Unauthorized");
        }
        return userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new CustomException(1002, "Unauthorized"));
    }
}
