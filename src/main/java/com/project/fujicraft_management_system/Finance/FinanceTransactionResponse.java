package com.project.fujicraft_management_system.Finance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FinanceTransactionResponse {
    private Long id;
    private LocalDate transactionDate;
    private LocalDate dueDate;
    private FinanceTransactionType type;
    private String category;
    private String description;
    private String referenceNumber;
    private Long invoiceId;
    private Long purchaseOrderId;
    private String partyName;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String currency;
    private FinanceStatus status;
    private String notes;
    private String createdBy;
}