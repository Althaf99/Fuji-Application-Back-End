package com.project.fujicraft_management_system.RawMaterial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RawMaterialRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String category;
    private String unit;
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal reorderLevel;
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal reorderQuantity;
    private String dosageRatio;
}