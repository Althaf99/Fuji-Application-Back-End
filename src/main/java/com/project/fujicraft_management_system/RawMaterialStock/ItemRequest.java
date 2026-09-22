package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @NotBlank
    private String type;
    private String color;
    private String size;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;
    @NotNull
    private Long vendorId;
}