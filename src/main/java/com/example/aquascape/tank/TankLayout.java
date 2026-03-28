package com.example.aquascape.tank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tank_layouts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tank_id", "version"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TankLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tank_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tank tank;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "preview_image_url", columnDefinition = "TEXT")
    private String previewImageUrl;

    @Column(name = "saved_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime savedAt;

    @Column(name = "layout_name", length = 120)
    private String layoutName;

    @PrePersist
    protected void onCreate() {
        if (savedAt == null) {
            savedAt = LocalDateTime.now();
        }
    }
}
