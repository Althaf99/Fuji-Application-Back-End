package com.project.fujicraft_management_system.RawMaterial.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class RawMaterialGRNDto {
    private Long id;
    private Long materialId;
    private String supplier;
    private String invoiceNo;
    private BigDecimal quantity;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private String createdBy;
    private Instant createdAt;
}