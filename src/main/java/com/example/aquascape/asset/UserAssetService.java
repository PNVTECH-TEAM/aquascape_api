package com.example.aquascape.asset;

import com.example.aquascape.asset.dto.UserAssetResponse;
import com.example.aquascape.auth.Auth;
import com.example.aquascape.storage.AzureStorageService;
import com.example.aquascape.asset.dto.UserAssetUploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAssetService {

    private final UserAssetRepository userAssetRepository;
    private final AzureStorageService azureStorageService;

    @Transactional
    public UserAssetResponse uploadAsset(Auth user, String name, MultipartFile glbFile, MultipartFile previewImage) {
        try {
            log.info("Uploading GLB file for user {}: {}", user.getId(), glbFile.getOriginalFilename());
            String glbUrl = azureStorageService.uploadFile(glbFile, "3d-models/" + user.getId());

            String previewUrl = null;
            if (previewImage != null && !previewImage.isEmpty()) {
                log.info("Uploading preview image for user {}: {}", user.getId(), previewImage.getOriginalFilename());
                previewUrl = azureStorageService.uploadFile(previewImage, "previews/" + user.getId());
            }

            UserAsset userAsset = UserAsset.builder()
                    .user(user)
                    .name(name)
                    .glbUrl(glbUrl)
                    .previewImageUrl(previewUrl)
                    .build();

            UserAsset savedAsset = userAssetRepository.save(userAsset);
            return mapToResponse(savedAsset);
        } catch (IOException e) {
            log.error("Failed to upload 3D model files for user {}", user.getId(), e);
            throw new RuntimeException("Failed to upload files to Azure Storage", e);
        }
    }

    public List<UserAssetResponse> getUserAssets(Long userId) {
        return userAssetRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public UserAssetResponse getAssetById(Long assetId, Long userId) {
        UserAsset asset = userAssetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
                
        if (!asset.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to access this asset");
        }
        
        return mapToResponse(asset);
    }
    
    @Transactional
    public void deleteAsset(Long assetId, Long userId) {
        UserAsset asset = userAssetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
                
        if (!asset.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this asset");
        }

        log.info("Deleting files from cloud for asset: {} (ID: {})", asset.getName(), assetId);
        
        if (asset.getGlbUrl() != null) {
            log.info("Deleting GLB file: {}", asset.getGlbUrl());
            azureStorageService.deleteFile(asset.getGlbUrl());
        }
        
        if (asset.getPreviewImageUrl() != null) {
            log.info("Deleting preview image: {}", asset.getPreviewImageUrl());
            azureStorageService.deleteFile(asset.getPreviewImageUrl());
        }
        
        log.info("Deleting asset record from database: ID {}", assetId);
        userAssetRepository.delete(asset);
    }

    private UserAssetResponse mapToResponse(UserAsset asset) {
        return UserAssetResponse.builder()
                .id(asset.getId())
                .userId(asset.getUser().getId())
                .name(asset.getName())
                .glbUrl(asset.getGlbUrl())
                .previewImageUrl(asset.getPreviewImageUrl())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
