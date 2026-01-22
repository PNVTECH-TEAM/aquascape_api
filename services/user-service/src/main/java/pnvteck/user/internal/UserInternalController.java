package pnvteck.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pnvteck.user.UserEntity;
import pnvteck.user.UserRepository;
import pnvteck.user.internal.dto.CreateUserRequest;
import pnvteck.user.internal.dto.UpdateStatusRequest;
import pnvteck.user.internal.dto.UserInternalResponse;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserInternalResponse createUser(@RequestBody CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email exists");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(request.getPasswordHash())
                .role(request.getRole())
                .status(request.getStatus())
                .build();

        UserEntity saved = userRepository.save(user);
        return mapInternal(saved);
    }

    @GetMapping("/by-username/{username}")
    public UserInternalResponse getByUsername(@PathVariable String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapInternal(user);
    }

    @GetMapping("/by-email")
    public UserInternalResponse getByEmail(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapInternal(user);
    }

    @PatchMapping("/{userId}/status")
    public UserInternalResponse updateStatus(@PathVariable Long userId,
                                              @RequestBody UpdateStatusRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(request.getStatus());
        return mapInternal(userRepository.save(user));
    }

    private UserInternalResponse mapInternal(UserEntity user) {
        return UserInternalResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .passwordHash(user.getPassword())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
