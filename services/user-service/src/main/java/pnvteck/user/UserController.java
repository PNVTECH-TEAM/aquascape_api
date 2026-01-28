package pnvteck.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pnvteck.common.response.ApiResponse;
import pnvteck.user.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable("userId") Long userId,
            @RequestHeader(value = "X-User-Name", required = false) String requesterUsername) {
        UserResponse result = userService.getUserById(userId, requesterUsername);

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Get user success")
                .result(result)
                .build();
    }

    @GetMapping("/all-users")
    public ApiResponse<List<UserResponse>> getAllUsers(
            @RequestHeader(value = "X-User-Name", required = false) String requesterUsername) {
        List<UserResponse> result = userService.getAllUsers(requesterUsername);

        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Get all users success")
                .result(result)
                .build();
    }
}
