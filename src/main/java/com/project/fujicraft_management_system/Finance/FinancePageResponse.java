package com.project.fujicraft_management_system.Finance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FinancePageResponse {
    private List<FinanceTransactionResponse> items;
    private int page;
    private int pageSize;
    private long totalItems;
    private int totalPages;
}