package com.project.fujicraft_management_system.Finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancePaymentRequest {
    @NotNull
    private LocalDate paymentDate;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
    @NotNull
    private PaymentMethod paymentMethod;
    private String referenceNumber;
}