package com.project.fujicraft_management_system.Finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FinancePaymentRepository extends JpaRepository<FinancePayment, Long> {
    @Query("select coalesce(sum(p.amount), 0) from FinancePayment p join p.transaction t where t.type = :type and t.status <> :voided and p.paymentDate between :start and :end and t.currency = :currency")
    BigDecimal sumPayments(@Param("type") FinanceTransactionType type, @Param("voided") FinanceStatus voided,
            @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("currency") String currency);

    @Query("select coalesce(sum(p.amount), 0) from FinancePayment p join p.transaction t where t.type = :type and t.status <> :voided and p.paymentDate between :start and :end and t.currency = :currency and (:category is null or t.category = :category) and (:status is null or t.status = :status) and (:partyName is null or lower(t.partyName) like lower(concat('%', :partyName, '%'))) and (:method is null or p.paymentMethod = :method)")
    BigDecimal sumFilteredPayments(@Param("type") FinanceTransactionType type, @Param("voided") FinanceStatus voided,
            @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("currency") String currency,
            @Param("category") String category, @Param("status") FinanceStatus status,
            @Param("partyName") String partyName, @Param("method") PaymentMethod method);

    @Query("select coalesce(sum(p.amount), 0) from FinancePayment p where p.transaction.id = :transactionId")
    BigDecimal sumForTransaction(@Param("transactionId") Long transactionId);

    boolean existsByTransactionIdAndReferenceNumber(Long transactionId, String referenceNumber);
}