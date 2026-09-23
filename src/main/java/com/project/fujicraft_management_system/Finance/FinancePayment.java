package com.project.fujicraft_management_system.Finance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_payments", indexes = {
        @Index(name = "idx_finance_payment_date", columnList = "payment_date"),
        @Index(name = "idx_finance_payment_transaction", columnList = "transaction_id")
})
@Data
public class FinancePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private FinanceTransaction transaction;
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;
    @Column(length = 150)
    private String referenceNumber;
    @Column(nullable = false, length = 255)
    private String createdBy;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}