package com.example.aquascape.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TankLayoutItemDto {
    private String id;
    private String tankLayoutId;
    private String instanceId;
    private String catalogItemId;
    private TransformDto transform;
}
