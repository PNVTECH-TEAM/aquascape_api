package com.example.aquascape.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDeleteResponse {
    private String message;
    private Long assetId;
    private LocalDateTime timestamp;
}
