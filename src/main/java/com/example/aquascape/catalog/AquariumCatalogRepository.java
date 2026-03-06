package com.example.aquascape.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AquariumCatalogRepository extends JpaRepository<AquariumCatalog, String> {
    List<AquariumCatalog> findByIsActiveTrue();
}
