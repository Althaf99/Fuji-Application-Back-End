package com.project.fujicraft_management_system.Finance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {
    @Mock
    FinanceTransactionRepository transactions;
    @Mock
    FinancePaymentRepository payments;
    @Mock
    FinanceAuditRepository audits;
    @InjectMocks
    FinanceService service;

    @Test
    void createsIncomeWithDecimalAmountAndActor() {
        when(transactions.save(any(FinanceTransaction.class))).thenAnswer(invocation -> {
            FinanceTransaction value = invocation.getArgument(0);
            value.setId(1L);
            return value;
        });
        var response = service.create(request(FinanceTransactionType.INCOME, FinanceStatus.RECEIVED, "100.25"),
                "alice");
        assertEquals(new BigDecimal("100.25"), response.getAmount());
        assertEquals("alice", response.getCreatedBy());
        verify(audits).save(any(FinanceAudit.class));
    }

    @Test
    void rejectsPaymentAboveTransactionAmount() {
        FinanceTransaction transaction = entity(FinanceTransactionType.EXPENSE, FinanceStatus.OUTSTANDING, "100");
        transaction.setId(2L);
        when(transactions.findById(2L)).thenReturn(java.util.Optional.of(transaction));
        when(payments.sumForTransaction(2L)).thenReturn(new BigDecimal("75"));
        var payment = new FinancePaymentRequest();
        payment.setPaymentDate(LocalDate.now());
        payment.setAmount(new BigDecimal("26"));
        payment.setPaymentMethod(PaymentMethod.CASH);
        assertThrows(FinanceModuleException.class, () -> service.recordPayment(2L, payment, "alice"));
        verify(payments, never()).save(any(FinancePayment.class));
    }

    @Test
    void voidPreservesTransactionAndAuditsAction() {
        FinanceTransaction transaction = entity(FinanceTransactionType.INCOME, FinanceStatus.RECEIVED, "100");
        transaction.setId(3L);
        when(transactions.findById(3L)).thenReturn(java.util.Optional.of(transaction));
        when(transactions.save(any(FinanceTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.voidTransaction(3L, "alice");
        assertEquals(FinanceStatus.VOIDED, response.getStatus());
        verify(transactions, never()).deleteById(3L);
        verify(audits).save(any(FinanceAudit.class));
    }

    private FinanceTransactionRequest request(FinanceTransactionType type, FinanceStatus status, String amount) {
        FinanceTransactionRequest value = new FinanceTransactionRequest();
        value.setTransactionDate(LocalDate.now());
        value.setType(type);
        value.setCategory("SALES");
        value.setDescription("Test");
        value.setReferenceNumber("REF-1");
        value.setPaymentMethod(PaymentMethod.CASH);
        value.setAmount(new BigDecimal(amount));
        value.setCurrency("LKR");
        value.setStatus(status);
        return value;
    }

    private FinanceTransaction entity(FinanceTransactionType type, FinanceStatus status, String amount) {
        FinanceTransaction value = new FinanceTransaction();
        value.setTransactionDate(LocalDate.now());
        value.setType(type);
        value.setCategory("SALES");
        value.setDescription("Test");
        value.setReferenceNumber("REF-1");
        value.setPaymentMethod(PaymentMethod.CASH);
        value.setAmount(new BigDecimal(amount));
        value.setCurrency("LKR");
        value.setStatus(status);
        return value;
    }
}