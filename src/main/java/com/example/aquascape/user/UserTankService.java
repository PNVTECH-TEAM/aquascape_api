package com.example.aquascape.user;

import com.example.aquascape.catalog.TankPreset;
import com.example.aquascape.tank.TankPresetDto;
import com.example.aquascape.tank.TankSizeDto;
import com.example.aquascape.tank.*;
import com.example.aquascape.user.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserTankService {

    private final TankRepository tankRepository;
    private final TankLayoutRepository tankLayoutRepository;
    private final TankLayoutItemRepository tankLayoutItemRepository;

    public UserTankService(TankRepository tankRepository,
                           TankLayoutRepository tankLayoutRepository,
                           TankLayoutItemRepository tankLayoutItemRepository) {
        this.tankRepository = tankRepository;
        this.tankLayoutRepository = tankLayoutRepository;
        this.tankLayoutItemRepository = tankLayoutItemRepository;
    }

    public List<UserTankDto> getUserTanks(Long userId) {
        List<Tank> userTanks = tankRepository.findByUserId(userId);

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
                        .previewImageUrl(layout.getPreviewImageUrl())
                        .tankLayoutItems(items.stream().map(this::mapLayoutItem).collect(Collectors.toList()))
                        .build();

                dto.setLayout(layoutDto);
            }

            return dto;
        }).collect(Collectors.toList());
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
                .transform(transform)
                .build();
    }
}
