package com.project.fujicraft_management_system.RawMaterial.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class RawMaterialStockEntryDto {
    private Long id;
    private Long materialId;
    private String materialName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private BigDecimal opening;
    private BigDecimal received;
    private BigDecimal consumed;
    private BigDecimal closing;
    private boolean submitted;
    private String enteredBy;
    private Instant enteredAt;
    private String updatedBy;
    private Instant updatedAt;
    private List<ConsumptionDto> consumption;
}