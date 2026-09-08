package com.project.fujicraft_management_system.RawMaterial;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_material_consumption_record")
@Data
public class ConsumptionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_entry_id", nullable = false)
    private RawMaterialStockEntry stockEntry;

    private String machine;
    private String mold;
    private String shift;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;
}