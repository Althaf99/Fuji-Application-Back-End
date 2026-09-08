package com.project.fujicraft_management_system.RawMaterial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RawMaterialGRNRequest {
    @NotNull
    private Long materialId;
    @NotBlank
    private String supplier;
    @NotBlank
    private String invoiceNo;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal quantity;
    @NotBlank
    private String date;
}