package com.project.fujicraft_management_system.RawMaterial.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RawMaterialStockEntryRequest {
    @NotNull
    private Long materialId;
    @NotBlank
    private String date;
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal opening;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal received;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal consumed;
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal closing;
    private boolean submitted;
    @Valid
    private List<ConsumptionRequest> consumption = new ArrayList<>();
}