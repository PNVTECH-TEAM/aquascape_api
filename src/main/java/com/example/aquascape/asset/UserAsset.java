package com.example.aquascape.asset;

import com.example.aquascape.auth.Auth;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_assets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Auth user;

    @Column(nullable = false)
    private String name;

    @Column(name = "glb_url", nullable = false)
    private String glbUrl;

    @Column(name = "preview_image_url")
    private String previewImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
