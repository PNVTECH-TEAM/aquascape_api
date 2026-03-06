package com.example.aquascape.tank;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.aquascape.catalog.TankPreset;

@Service
public class TankPresetService {

    private final TankPresetRepository repository;

    public TankPresetService(TankPresetRepository repository) {
        this.repository = repository;
    }

    public List<TankPresetDto> getAllPresets() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TankPresetDto mapToDto(TankPreset entity) {
        return TankPresetDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .size(TankSizeDto.builder()
                        .width(entity.getWidthCm())
                        .height(entity.getHeightCm())
                        .depth(entity.getDepthCm())
                        .build())
                .build();
    }
}
