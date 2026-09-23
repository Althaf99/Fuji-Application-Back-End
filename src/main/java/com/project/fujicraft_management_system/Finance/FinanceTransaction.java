package com.project.fujicraft_management_system.Finance;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "finance_transactions", indexes = {
        @Index(name = "idx_finance_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_finance_due_date", columnList = "due_date"),
        @Index(name = "idx_finance_type", columnList = "type"),
        @Index(name = "idx_finance_category", columnList = "category"),
        @Index(name = "idx_finance_status", columnList = "status"),
        @Index(name = "idx_finance_payment_method", columnList = "payment_method"),
        @Index(name = "idx_finance_invoice", columnList = "invoice_id"),
        @Index(name = "idx_finance_purchase_order", columnList = "purchase_order_id")
})
@Data
public class FinanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinanceTransactionType type;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(nullable = false, length = 500)
    private String description;
    @Column(name = "reference_number", nullable = false, length = 150)
    private String referenceNumber;
    @Column(name = "invoice_id")
    private Long invoiceId;
    @Column(name = "purchase_order_id")
    private Long purchaseOrderId;
    @Column(length = 255)
    private String partyName;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency = "LKR";
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinanceStatus status;
    @Column(columnDefinition = "text")
    private String notes;
    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;
    @Column(name = "updated_by", nullable = false, length = 255)
    private String updatedBy;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private LocalDateTime voidedAt;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancePayment> payments = new ArrayList<>();

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}