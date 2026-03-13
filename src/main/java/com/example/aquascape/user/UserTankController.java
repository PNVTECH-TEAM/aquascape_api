package com.example.aquascape.user;

import com.example.aquascape.auth.Auth;
import com.example.aquascape.user.dto.TankMetadataDto;
import com.example.aquascape.user.dto.UserTankDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<UserTankDto>> getUserTanks(@AuthenticationPrincipal Auth user) {
        return ResponseEntity.ok(userTankService.getUserTanks(user.getId()));
    }

    @GetMapping("/versions")
    public ResponseEntity<List<TankMetadataDto>> getUserTankVersions(
            @AuthenticationPrincipal Auth user,
            @RequestParam("presetId") String presetId) {
        return ResponseEntity.ok(userTankService.getUserTankVersions(user.getId(), presetId));
    }

    @GetMapping("/layouts/{layoutId}")
    public ResponseEntity<com.example.aquascape.user.dto.TankLayoutDto> getLayoutDetails(
            @AuthenticationPrincipal Auth user,
            @PathVariable java.util.UUID layoutId) {
        return ResponseEntity.ok(userTankService.getLayoutDetails(user.getId(), layoutId));
    }

    @PostMapping
    public ResponseEntity<UserTankDto> saveUserTank(@AuthenticationPrincipal Auth user,
                                                    @RequestBody com.example.aquascape.user.dto.SaveTankRequest request) {
        return ResponseEntity.ok(userTankService.saveUserTank(user.getId(), request));
    }
}
