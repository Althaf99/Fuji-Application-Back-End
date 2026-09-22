package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GrnRequest {
    private String grnNumber;
    @NotNull
    private LocalDate date;
    @NotNull
    private Long vendorId;
    private Long purchaseOrderId;
    private String notes;
    @NotEmpty
    @Valid
    private List<GrnItemRequest> items;
}