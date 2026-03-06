package com.example.aquascape.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "aquarium_catalog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AquariumCatalog {

    @Id
    @Column(length = 80)
    private String id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT", name = "image_url")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TEXT", name = "model_url")
    private String modelUrl;

    @Column(length = 80)
    private String category;

    @Column(length = 50)
    private String type;

    @Builder.Default
    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
