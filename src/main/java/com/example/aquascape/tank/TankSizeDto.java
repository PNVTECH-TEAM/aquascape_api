package com.example.aquascape.tank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TankSizeDto {
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
}
