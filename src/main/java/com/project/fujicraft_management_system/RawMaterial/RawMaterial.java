package com.project.fujicraft_management_system.RawMaterial;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "raw_material", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Data
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String category;
    private String unit = "kg";
    private BigDecimal reorderLevel;
    private BigDecimal reorderQuantity;
    private String dosageRatio;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}