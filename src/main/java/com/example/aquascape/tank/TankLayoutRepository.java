package com.example.aquascape.tank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TankLayoutRepository extends JpaRepository<TankLayout, UUID> {
    Optional<TankLayout> findByTankIdAndVersion(UUID tankId, Integer version);

    @org.springframework.data.jpa.repository.Query("SELECT tl FROM TankLayout tl JOIN tl.tank t WHERE t.userId = :userId AND t.preset.id = :presetId ORDER BY tl.savedAt DESC")
    java.util.List<TankLayout> findByUserIdAndPresetId(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("presetId") String presetId);

    @org.springframework.data.jpa.repository.Query("SELECT tl FROM TankLayout tl JOIN tl.tank t WHERE t.userId = :userId ORDER BY tl.savedAt DESC")
    java.util.List<TankLayout> findByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
