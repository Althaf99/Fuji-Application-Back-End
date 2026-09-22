package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ConsumptionRequest {
    private String consumptionNumber;
    @NotNull
    private LocalDate date;
    @NotNull
    private ItemType itemType;
    @NotNull
    private Long itemId;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;
    @NotBlank
    private String machineNo;
    @NotBlank
    private String moldNo;
    @NotBlank
    private String usedBy;
    private String notes;
}