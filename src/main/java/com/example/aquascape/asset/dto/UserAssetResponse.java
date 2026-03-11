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
public class UserAssetResponse {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private String glbUrl;
    private String previewImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
