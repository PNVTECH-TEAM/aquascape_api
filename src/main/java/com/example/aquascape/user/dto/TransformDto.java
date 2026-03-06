package com.example.aquascape.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformDto {
    private CoordinateDto position;
    private CoordinateDto rotation;
    private CoordinateDto scale;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoordinateDto {
        private BigDecimal x;
        private BigDecimal y;
        private BigDecimal z;
    }
}
