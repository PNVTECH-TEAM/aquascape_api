package com.example.aquascape.tank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aquarium/presets")
public class TankPresetController {

    private final TankPresetService service;

    public TankPresetController(TankPresetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TankPresetDto>> getAllPresets() {
        return ResponseEntity.ok(service.getAllPresets());
    }
}
