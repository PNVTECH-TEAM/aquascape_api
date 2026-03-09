package com.example.aquascape.asset;

import com.example.aquascape.asset.dto.UserAssetResponse;
import com.example.aquascape.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-assets")
@RequiredArgsConstructor
public class UserAssetController {

    private final UserAssetService userAssetService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserAssetResponse> uploadAsset(
            @AuthenticationPrincipal Auth user,
            @RequestParam("name") String name,
            @RequestParam("glbFile") MultipartFile glbFile,
            @RequestParam(value = "previewImage", required = false) MultipartFile previewImage) {
            
        UserAssetResponse response = userAssetService.uploadAsset(user, name, glbFile, previewImage);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserAssetResponse>> getUserAssets(@AuthenticationPrincipal Auth user) {
        List<UserAssetResponse> assets = userAssetService.getUserAssets(user.getId());
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAssetResponse> getAssetById(
            @PathVariable Long id,
            @AuthenticationPrincipal Auth user) {
        UserAssetResponse asset = userAssetService.getAssetById(id, user.getId());
        return ResponseEntity.ok(asset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long id,
            @AuthenticationPrincipal Auth user) {
        userAssetService.deleteAsset(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
