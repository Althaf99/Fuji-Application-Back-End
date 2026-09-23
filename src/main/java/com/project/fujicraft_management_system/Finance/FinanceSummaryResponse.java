package com.project.fujicraft_management_system.Finance;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class FinanceSummaryResponse {
    private String currency;
    private Period period;
    private Period comparisonPeriod;
    private Metrics current;
    private Metrics comparison;
    private Changes changes;
    private List<ExpenseBreakdown> expenseBreakdown;

    @Data
    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;

        public Period(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    @Data
    public static class Metrics {
        private BigDecimal revenue = BigDecimal.ZERO;
        private BigDecimal expenses = BigDecimal.ZERO;
        private BigDecimal profit = BigDecimal.ZERO;
        private BigDecimal profitMargin = BigDecimal.ZERO;
        private BigDecimal cashIn = BigDecimal.ZERO;
        private BigDecimal cashOut = BigDecimal.ZERO;
        private BigDecimal cashFlow = BigDecimal.ZERO;
        private BigDecimal receivables = BigDecimal.ZERO;
        private BigDecimal invoicesIssued = BigDecimal.ZERO;
        private BigDecimal paymentsReceived = BigDecimal.ZERO;
    }

    @Data
    public static class Changes {
        private BigDecimal revenuePercent = BigDecimal.ZERO;
        private BigDecimal expensesPercent = BigDecimal.ZERO;
        private BigDecimal cashFlowPercent = BigDecimal.ZERO;
        private BigDecimal profitMarginPercent = BigDecimal.ZERO;
    }

    @Data
    public static class ExpenseBreakdown {
        private String category;
        private BigDecimal amount;
        private BigDecimal percentage;

        public ExpenseBreakdown(String category, BigDecimal amount, BigDecimal percentage) {
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
        }
    }
}