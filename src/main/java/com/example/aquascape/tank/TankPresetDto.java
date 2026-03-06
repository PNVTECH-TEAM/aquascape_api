package com.example.aquascape.tank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TankPresetDto {
    private String id;
    private String name;
    private TankSizeDto size;
}
