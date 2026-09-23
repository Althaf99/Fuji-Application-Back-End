package com.project.fujicraft_management_system.Finance;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FinanceSpecifications {
    private FinanceSpecifications() {
    }

    public static Specification<FinanceTransaction> between(LocalDate from, LocalDate to) {
        return (root, query, cb) -> cb.between(root.get("transactionDate"), from, to);
    }

    public static Specification<FinanceTransaction> type(FinanceTransactionType value) {
        return value == null ? null : (root, query, cb) -> cb.equal(root.get("type"), value);
    }

    public static Specification<FinanceTransaction> category(String value) {
        return value == null || value.isBlank() ? null
                : (root, query, cb) -> cb.equal(cb.lower(root.get("category")), value.toLowerCase());
    }

    public static Specification<FinanceTransaction> status(FinanceStatus value) {
        return value == null ? null : (root, query, cb) -> cb.equal(root.get("status"), value);
    }

    public static Specification<FinanceTransaction> paymentMethod(PaymentMethod value) {
        return value == null ? null : (root, query, cb) -> cb.equal(root.get("paymentMethod"), value);
    }

    public static Specification<FinanceTransaction> partyName(String value) {
        return value == null || value.isBlank() ? null
                : (root, query, cb) -> cb.like(cb.lower(root.get("partyName")), "%" + value.toLowerCase() + "%");
    }

    public static Specification<FinanceTransaction> search(String value) {
        return value == null || value.isBlank() ? null
                : (root, query, cb) -> cb.or(
                        cb.like(cb.lower(root.get("description")), "%" + value.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("referenceNumber")), "%" + value.toLowerCase() + "%"));
    }

    public static Specification<FinanceTransaction> notVoided() {
        return (root, query, cb) -> cb.notEqual(root.get("status"), FinanceStatus.VOIDED);
    }
}