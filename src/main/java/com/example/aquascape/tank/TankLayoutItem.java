package com.example.aquascape.tank;

import com.example.aquascape.catalog.AquariumCatalog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tank_layout_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TankLayoutItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layout_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TankLayout layout;

    @Column(name = "instance_id", nullable = false, length = 120)
    private String instanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_item_id")
    private AquariumCatalog catalogItem;
    
    @Column(name = "user_asset_id")
    private Long userAssetId;

    @Column(name = "pos_x", precision = 10, scale = 4)
    private BigDecimal posX;

    @Column(name = "pos_y", precision = 10, scale = 4)
    private BigDecimal posY;

    @Column(name = "pos_z", precision = 10, scale = 4)
    private BigDecimal posZ;

    @Column(name = "rot_x", precision = 10, scale = 4)
    private BigDecimal rotX;

    @Column(name = "rot_y", precision = 10, scale = 4)
    private BigDecimal rotY;

    @Column(name = "rot_z", precision = 10, scale = 4)
    private BigDecimal rotZ;

    @Column(name = "scale_x", precision = 10, scale = 4)
    private BigDecimal scaleX;

    @Column(name = "scale_y", precision = 10, scale = 4)
    private BigDecimal scaleY;

    @Column(name = "scale_z", precision = 10, scale = 4)
    private BigDecimal scaleZ;
}
