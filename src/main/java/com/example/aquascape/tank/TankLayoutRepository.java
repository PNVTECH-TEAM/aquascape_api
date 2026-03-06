package com.example.aquascape.tank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TankLayoutRepository extends JpaRepository<TankLayout, UUID> {
    Optional<TankLayout> findByTankIdAndVersion(UUID tankId, Integer version);
}
