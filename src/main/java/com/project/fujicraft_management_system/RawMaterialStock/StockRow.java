package com.project.fujicraft_management_system.RawMaterialStock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StockRow {
    private Long itemId;
    private ItemType itemType;
    private String itemCode;
    private BigDecimal totalReceived;
    private BigDecimal totalConsumed;
    private BigDecimal availableQuantity;
    private LocalDate lastReceivedDate;
    private LocalDate lastConsumedDate;
}