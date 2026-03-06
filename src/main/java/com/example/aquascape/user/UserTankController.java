package com.example.aquascape.user;

import com.example.aquascape.user.dto.UserTankDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-tanks")
public class UserTankController {

    private final UserTankService userTankService;

    public UserTankController(UserTankService userTankService) {
        this.userTankService = userTankService;
    }

    @GetMapping
    public ResponseEntity<List<UserTankDto>> getUserTanks(@RequestParam(required = false, defaultValue = "1") Long userId) {
        // Here we mock userId = 1 as default if not provided until full JWT context takes over.
        return ResponseEntity.ok(userTankService.getUserTanks(userId));
    }
}
