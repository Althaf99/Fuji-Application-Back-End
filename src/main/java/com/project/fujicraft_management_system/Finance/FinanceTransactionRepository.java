package com.project.fujicraft_management_system.Finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FinanceTransactionRepository
        extends JpaRepository<FinanceTransaction, Long>, JpaSpecificationExecutor<FinanceTransaction> {
    List<FinanceTransaction> findByDueDateBetweenAndStatusIn(java.time.LocalDate from, java.time.LocalDate to,
            List<FinanceStatus> statuses);

    boolean existsByInvoiceIdAndTypeAndStatusNot(Long invoiceId, FinanceTransactionType type, FinanceStatus status);
}