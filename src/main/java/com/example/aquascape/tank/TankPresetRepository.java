package com.example.aquascape.tank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.aquascape.catalog.TankPreset;

@Repository
public interface TankPresetRepository extends JpaRepository<TankPreset, String> {
}
