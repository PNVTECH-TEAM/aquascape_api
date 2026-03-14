package com.example.aquascape.tank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TankRepository extends JpaRepository<Tank, UUID> {
    List<Tank> findByUserId(Long userId);
    
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Tank t WHERE t.userId = :userId AND t.preset.id = :presetId")
    List<Tank> findByUserIdAndPresetId(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("presetId") String presetId);
}
