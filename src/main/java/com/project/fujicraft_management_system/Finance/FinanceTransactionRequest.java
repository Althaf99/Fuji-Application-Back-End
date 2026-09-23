package com.project.fujicraft_management_system.Finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinanceTransactionRequest {
    @NotNull
    private LocalDate transactionDate;
    private LocalDate dueDate;
    @NotNull
    private FinanceTransactionType type;
    @NotBlank
    private String category;
    @NotBlank
    private String description;
    @NotBlank
    private String referenceNumber;
    private Long invoiceId;
    private Long purchaseOrderId;
    private String partyName;
    @NotNull
    private PaymentMethod paymentMethod;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
    private String currency = "LKR";
    @NotNull
    private FinanceStatus status;
    private String notes;
}