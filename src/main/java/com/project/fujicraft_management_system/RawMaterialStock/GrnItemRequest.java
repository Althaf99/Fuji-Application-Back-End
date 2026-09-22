package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GrnItemRequest {
    @NotNull
    private ItemType itemType;
    @NotNull
    private Long itemId;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;
}