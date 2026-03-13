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
    private final GltfProcessorService gltfProcessorService;
    private final ImageProcessorService imageProcessorService;

    @Transactional
    public UserAssetResponse uploadAsset(Auth user, String name, String type, MultipartFile glbFile, MultipartFile previewImage) {
        try {
            log.info("Processing and uploading GLB file for user {}: {}", user.getId(), glbFile.getOriginalFilename());
            
            byte[] processedGlb = gltfProcessorService.compressGlb(glbFile);
            
            String originalFilename = glbFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".glb";
            String glbPath = "3d-models/" + user.getId() + "/" + java.util.UUID.randomUUID().toString() + extension;
            
            String glbUrl = azureStorageService.uploadFile(processedGlb, glbPath);

            String previewUrl = null;
            if (previewImage != null && !previewImage.isEmpty()) {
                log.info("Processing and uploading preview image for user {}: {}", user.getId(), previewImage.getOriginalFilename());
                
                ImageProcessorService.ProcessedImage processedImage = imageProcessorService.compressPreviewImage(previewImage);
                
                String imgExtension = processedImage.getExtension();
                String imgPath = "previews/" + user.getId() + "/" + java.util.UUID.randomUUID().toString() + imgExtension;
                
                previewUrl = azureStorageService.uploadFile(processedImage.getData(), imgPath);
            }

            UserAsset userAsset = UserAsset.builder()
                    .user(user)
                    .name(name)
                    .type(type)
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
                .type(asset.getType())
                .name(asset.getName())
                .glbUrl(asset.getGlbUrl())
                .previewImageUrl(asset.getPreviewImageUrl())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
