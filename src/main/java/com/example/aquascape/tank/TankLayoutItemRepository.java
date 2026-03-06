package com.example.aquascape.tank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TankLayoutItemRepository extends JpaRepository<TankLayoutItem, UUID> {
    List<TankLayoutItem> findByLayoutId(UUID layoutId);
}
