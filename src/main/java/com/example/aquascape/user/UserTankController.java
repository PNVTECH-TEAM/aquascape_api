package com.example.aquascape.user;

import com.example.aquascape.user.dto.UserTankDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user-tanks")
public class UserTankController {

    private final UserTankService userTankService;

    public UserTankController(UserTankService userTankService) {
        this.userTankService = userTankService;
    }

    @GetMapping
    public ResponseEntity<List<UserTankDto>> getUserTanks(@RequestParam(required = false, defaultValue = "1") Long userId) {
        return ResponseEntity.ok(userTankService.getUserTanks(userId));
    }

    @PostMapping
    public ResponseEntity<UserTankDto> saveUserTank(@RequestParam(required = false, defaultValue = "1") Long userId,
                                                    @RequestBody com.example.aquascape.user.dto.SaveTankRequest request) {
        return ResponseEntity.ok(userTankService.saveUserTank(userId, request));
    }
}
