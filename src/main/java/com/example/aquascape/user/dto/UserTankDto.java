package com.example.aquascape.user.dto;

import com.example.aquascape.tank.TankPresetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTankDto {
    private String id;
    private Long userId;
    private String name;
    private TankPresetDto preset;
    private Integer latestLayoutVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private TankLayoutDto layout;
}
