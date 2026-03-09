package com.example.aquascape.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {
    List<UserAsset> findByUserId(Long userId);
}
