package com.project.fujicraft_management_system.Finance;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "finance_audit_history", indexes = @Index(name = "idx_finance_audit_transaction", columnList = "transaction_id"))
@Data
public class FinanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;
    @Column(nullable = false, length = 30)
    private String action;
    @Column(nullable = false, length = 255)
    private String actor;
    @Column(columnDefinition = "text")
    private String details;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}