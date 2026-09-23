package com.project.fujicraft_management_system.Finance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DueResponse {
    private List<FinanceTransactionResponse> overdue;
    private List<FinanceTransactionResponse> dueToday;
    private List<FinanceTransactionResponse> upcoming;
}