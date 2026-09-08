package com.project.fujicraft_management_system.RawMaterial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsumptionRequest {
    private String machine;
    private String mold;
    private String shift;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal quantity;
}