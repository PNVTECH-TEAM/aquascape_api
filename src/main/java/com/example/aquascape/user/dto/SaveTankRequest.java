package com.example.aquascape.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveTankRequest {
    private String id;
    private String name;
    private String presetId;
    private String previewImageUrl;
    private List<TankLayoutItemDto> items;
}
