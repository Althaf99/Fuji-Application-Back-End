package com.project.fujicraft_management_system.RawMaterial;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "raw_material_grn")
@Data
public class RawMaterialGRN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial material;

    private String supplier;
    private String invoiceNo;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;
    @Column(nullable = false)
    private LocalDate date;
    private String createdBy;
    private Instant createdAt;
}