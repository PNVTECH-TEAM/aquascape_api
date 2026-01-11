package pnvteck.all_in_one.user;

import lombok.RequiredArgsConstructor;
import pnvteck.all_in_one.common.response.ApiResponse;
import pnvteck.all_in_one.user.dto.UserResponse;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") Long userId) {
        UserResponse result = userService.getUserById(userId);

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Get user success")
                .result(result)
                .build();
    }

    @GetMapping("/all-users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> result = userService.getAllUsers();

        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Get all users success")
                .result(result)
                .build();
    }
}