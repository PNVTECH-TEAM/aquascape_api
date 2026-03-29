package com.example.aquascape.user;

import com.example.aquascape.catalog.TankPreset;
import com.example.aquascape.tank.TankPresetDto;
import com.example.aquascape.tank.TankSizeDto;
import com.example.aquascape.tank.*;
import com.example.aquascape.user.dto.*;
import org.springframework.stereotype.Service;

import com.example.aquascape.catalog.AquariumCatalogRepository;
import com.example.aquascape.catalog.AquariumCatalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserTankService {

    private final TankRepository tankRepository;
    private final TankLayoutRepository tankLayoutRepository;
    private final TankLayoutItemRepository tankLayoutItemRepository;
    private final TankPresetRepository tankPresetRepository;
    private final AquariumCatalogRepository catalogRepository;
    private final com.example.aquascape.storage.AzureStorageService azureStorageService;

    public UserTankService(TankRepository tankRepository,
                           TankLayoutRepository tankLayoutRepository,
                           TankLayoutItemRepository tankLayoutItemRepository,
                           TankPresetRepository tankPresetRepository,
                           AquariumCatalogRepository catalogRepository,
                           com.example.aquascape.storage.AzureStorageService azureStorageService) {
        this.tankRepository = tankRepository;
        this.tankLayoutRepository = tankLayoutRepository;
        this.tankLayoutItemRepository = tankLayoutItemRepository;
        this.tankPresetRepository = tankPresetRepository;
        this.catalogRepository = catalogRepository;
        this.azureStorageService = azureStorageService;
    }

    public List<TankMetadataDto> getUserTankVersions(Long userId, String presetId) {
        List<TankLayout> layouts;
        if (presetId != null && !presetId.isEmpty()) {
            layouts = tankLayoutRepository.findByUserIdAndPresetId(userId, presetId);
        } else {
            layouts = tankLayoutRepository.findByUserId(userId);
        }
        return layouts.stream().map(layout -> TankMetadataDto.builder()
                .layoutId(layout.getId().toString())
                .tankId(layout.getTank().getId().toString())
                .tankName(layout.getLayoutName() != null ? layout.getLayoutName() : layout.getTank().getName())
                .version(layout.getVersion())
                .previewImageUrl(layout.getPreviewImageUrl() != null ? layout.getPreviewImageUrl().replace("%2F", "/") : null)
                .savedAt(layout.getSavedAt())
                .build()
        ).collect(Collectors.toList());
    }

    @org.springframework.cache.annotation.Cacheable(value = "user_tanks", key = "#userId + (#presetId != null ? '_' + #presetId : '')")
    public List<UserTankDto> getUserTanks(Long userId, String presetId) {
        List<Tank> userTanks;
        if (presetId != null && !presetId.isEmpty()) {
            userTanks = tankRepository.findByUserIdAndPresetId(userId, presetId);
        } else {
            userTanks = tankRepository.findByUserId(userId);
        }

        return userTanks.stream().map(tank -> {
            UserTankDto dto = mapTank(tank);

            Optional<TankLayout> activeLayoutOpt = tankLayoutRepository.findByTankIdAndVersion(
                    tank.getId(), tank.getLatestLayoutVersion() != null ? tank.getLatestLayoutVersion() : 1);

            if (activeLayoutOpt.isPresent()) {
                TankLayout layout = activeLayoutOpt.get();
                List<TankLayoutItem> items = tankLayoutItemRepository.findByLayoutId(layout.getId());
                
                TankLayoutDto layoutDto = TankLayoutDto.builder()
                        .id(layout.getId().toString())
                        .version(layout.getVersion())
                        .previewImageUrl(layout.getPreviewImageUrl() != null ? layout.getPreviewImageUrl().replace("%2F", "/") : null)
                        .tankLayoutItems(items.stream().map(this::mapLayoutItem).collect(Collectors.toList()))
                        .build();

                dto.setLayout(layoutDto);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @org.springframework.cache.annotation.Cacheable(value = "tank_layout", key = "#layoutId.toString()")
    public TankLayoutDto getLayoutDetails(Long userId, UUID layoutId) {
        TankLayout layout = tankLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("Layout not found"));

        if (!layout.getTank().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to this layout");
        }

        List<TankLayoutItem> items = tankLayoutItemRepository.findByLayoutId(layout.getId());

        return TankLayoutDto.builder()
                .id(layout.getId().toString())
                .version(layout.getVersion())
                .previewImageUrl(layout.getPreviewImageUrl() != null ? layout.getPreviewImageUrl().replace("%2F", "/") : null)
                .tankLayoutItems(items.stream().map(this::mapLayoutItem).collect(Collectors.toList()))
                .build();
    }

    private UserTankDto mapTank(Tank tank) {
        return UserTankDto.builder()
                .id(tank.getId().toString())
                .userId(tank.getUserId())
                .name(tank.getName())
                .latestLayoutVersion(tank.getLatestLayoutVersion())
                .createdAt(tank.getCreatedAt())
                .updatedAt(tank.getUpdatedAt())
                .preset(mapPreset(tank.getPreset()))
                .build();
    }

    private TankPresetDto mapPreset(TankPreset preset) {
        if (preset == null) return null;
        return TankPresetDto.builder()
                .id(preset.getId())
                .name(preset.getName())
                .size(TankSizeDto.builder()
                        .width(preset.getWidthCm())
                        .height(preset.getHeightCm())
                        .depth(preset.getDepthCm())
                        .build())
                .build();
    }

    private TankLayoutItemDto mapLayoutItem(TankLayoutItem item) {
        TransformDto.CoordinateDto pos = TransformDto.CoordinateDto.builder()
                .x(item.getPosX())
                .y(item.getPosY())
                .z(item.getPosZ())
                .build();

        TransformDto.CoordinateDto rot = TransformDto.CoordinateDto.builder()
                .x(item.getRotX())
                .y(item.getRotY())
                .z(item.getRotZ())
                .build();

        TransformDto.CoordinateDto scale = TransformDto.CoordinateDto.builder()
                .x(item.getScaleX())
                .y(item.getScaleY())
                .z(item.getScaleZ())
                .build();

        TransformDto transform = TransformDto.builder()
                .position(pos)
                .rotation(rot)
                .scale(scale)
                .build();

        return TankLayoutItemDto.builder()
                .id(item.getId().toString())
                .tankLayoutId(item.getLayout() != null ? item.getLayout().getId().toString() : null)
                .instanceId(item.getInstanceId())
                .catalogItemId(item.getCatalogItem() != null ? item.getCatalogItem().getId() : null)
                .userAssetId(item.getUserAssetId())
                .transform(transform)
                .build();
    }

    @org.springframework.cache.annotation.CacheEvict(value = "user_tanks", allEntries = true)
    public UserTankDto saveUserTank(Long userId, SaveTankRequest request) {
        String finalPreviewUrl = request.getPreviewImageUrl();
        if (finalPreviewUrl != null && finalPreviewUrl.startsWith("data:image")) {
            String[] parts = finalPreviewUrl.split(",");
            if (parts.length > 1) {
                try {
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(parts[1]);
                    String extension = ".png"; 
                    if (parts[0].contains("jpeg") || parts[0].contains("jpg")) extension = ".jpg";
                    else if (parts[0].contains("webp")) extension = ".webp";
                    
                    String fileName = "tank-previews/" + userId + "/" + java.util.UUID.randomUUID().toString() + extension;
                    finalPreviewUrl = azureStorageService.uploadFile(imageBytes, fileName);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload base64 image to Azure Storage", e);
                }
            }
        }

        Tank tank;
        if (request.getId() != null && !request.getId().isEmpty()) {
            tank = tankRepository.findById(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Tank not found"));
            
            if (!tank.getUserId().equals(userId)) {
                throw new IllegalArgumentException("You don't have permission to edit this tank");
            }
            if (request.getName() != null) tank.setName(request.getName());
            if (request.getPresetId() != null) {
                tank.setPreset(tankPresetRepository.findById(request.getPresetId())
                        .orElseThrow(() -> new IllegalArgumentException("Tank Size (Preset) not found with ID: " + request.getPresetId())));
            }
            
            tank.setLatestLayoutVersion(tank.getLatestLayoutVersion() + 1);
        } else {
            tank = new Tank();
            tank.setUserId(userId);
            tank.setName(request.getName() != null ? request.getName() : "Untilted Tank");
            tank.setLatestLayoutVersion(1);
            if (request.getPresetId() != null) {
                tank.setPreset(tankPresetRepository.findById(request.getPresetId())
                        .orElseThrow(() -> new IllegalArgumentException("Tank Size (Preset) not found with ID: " + request.getPresetId())));
            }
        }

        tank = tankRepository.save(tank);

        TankLayout layout = new TankLayout();
        layout.setTank(tank);
        layout.setVersion(tank.getLatestLayoutVersion());
        layout.setPreviewImageUrl(finalPreviewUrl);
        layout.setLayoutName(request.getName() != null ? request.getName() : tank.getName());
        layout = tankLayoutRepository.save(layout);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            TankLayout finalLayout = layout;
            List<TankLayoutItem> entities = request.getItems().stream().map(dto -> {
                TankLayoutItem item = new TankLayoutItem();
                item.setLayout(finalLayout);
                item.setInstanceId(dto.getInstanceId());
                if (dto.getCatalogItemId() != null) {
                    item.setCatalogItem(catalogRepository.findById(dto.getCatalogItemId()).orElse(null));
                }
                
                if (dto.getUserAssetId() != null) {
                    item.setUserAssetId(dto.getUserAssetId());
                }
                
                if (dto.getTransform() != null) {
                    if (dto.getTransform().getPosition() != null) {
                        item.setPosX(dto.getTransform().getPosition().getX());
                        item.setPosY(dto.getTransform().getPosition().getY());
                        item.setPosZ(dto.getTransform().getPosition().getZ());
                    }
                    if (dto.getTransform().getRotation() != null) {
                        item.setRotX(dto.getTransform().getRotation().getX());
                        item.setRotY(dto.getTransform().getRotation().getY());
                        item.setRotZ(dto.getTransform().getRotation().getZ());
                    }
                    if (dto.getTransform().getScale() != null) {
                        item.setScaleX(dto.getTransform().getScale().getX());
                        item.setScaleY(dto.getTransform().getScale().getY());
                        item.setScaleZ(dto.getTransform().getScale().getZ());
                    }
                }
                return item;
            }).collect(Collectors.toList());
            
            tankLayoutItemRepository.saveAll(entities);
        }

        final Tank finalTank = tank;
        return getUserTanks(userId, null).stream()
                .filter(t -> t.getId().equals(finalTank.getId().toString()))
                .findFirst()
                .orElse(null);
    }
}
