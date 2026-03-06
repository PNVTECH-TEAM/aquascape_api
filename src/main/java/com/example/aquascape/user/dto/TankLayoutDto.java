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
public class TankLayoutDto {
    private String id;
    private Integer version;
    private String previewImageUrl;
    private List<TankLayoutItemDto> tankLayoutItems;
}
