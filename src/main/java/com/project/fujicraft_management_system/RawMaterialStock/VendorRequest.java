package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VendorRequest {
    @NotBlank
    private String name;
    private String location;
    private String contactPerson;
    private String contactNumber;
    private String bankName;
    private String bankAccountNumber;
}