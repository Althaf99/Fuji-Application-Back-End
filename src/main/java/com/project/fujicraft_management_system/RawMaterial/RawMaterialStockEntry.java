package com.project.fujicraft_management_system.RawMaterial;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raw_material_stock_entry", uniqueConstraints = @UniqueConstraint(columnNames = {"material_id", "date"}))
@Data
public class RawMaterialStockEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial material;

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal opening = BigDecimal.ZERO;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal received = BigDecimal.ZERO;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal consumed = BigDecimal.ZERO;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal closing = BigDecimal.ZERO;
    @Column(nullable = false)
    private boolean submitted;
    private String enteredBy;
    private Instant enteredAt;
    private String updatedBy;
    private Instant updatedAt;

    @OneToMany(mappedBy = "stockEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumptionRecord> consumption = new ArrayList<>();
}