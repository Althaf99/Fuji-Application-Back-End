package com.project.fujicraft_management_system.RawMaterial.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class RawMaterialDto {
    private Long id;
    private String code;
    private String name;
    private String category;
    private String unit;
    private BigDecimal reorderLevel;
    private BigDecimal reorderQuantity;
    private String dosageRatio;
    private Instant createdAt;
    private Instant updatedAt;
}