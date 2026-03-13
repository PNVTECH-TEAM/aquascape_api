package com.example.aquascape.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TankMetadataDto {
    private String layoutId;
    private String tankId;
    private String tankName;
    private Integer version;
    private String previewImageUrl;
    private LocalDateTime savedAt;
}
