package com.project.fujicraft_management_system.RawMaterial.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsumptionDto {
    private Long id;
    private String machine;
    private String mold;
    private String shift;
    private BigDecimal quantity;
}