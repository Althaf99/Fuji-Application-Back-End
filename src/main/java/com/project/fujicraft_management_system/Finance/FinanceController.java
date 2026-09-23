package com.project.fujicraft_management_system.Finance;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceController {
    private final FinanceService service;

    public FinanceController(FinanceService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public FinanceSummaryResponse summary(@RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) LocalDate comparisonStartDate,
            @RequestParam(required = false) LocalDate comparisonEndDate,
            @RequestParam(required = false) FinanceTransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) FinanceStatus status,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) String partyName) {
        return service.summary(startDate, endDate, comparisonStartDate, comparisonEndDate, type, category, status,
                paymentMethod, partyName);
    }

    @GetMapping("/transactions")
    public FinancePageResponse list(@RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) FinanceTransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) FinanceStatus status,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) String partyName, @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return service.list(startDate, endDate, type, category, status, paymentMethod, partyName, search, page,
                pageSize, sortBy, sortDirection);
    }

    @GetMapping("/transactions/{id}")
    public FinanceTransactionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceTransactionResponse create(@Valid @RequestBody FinanceTransactionRequest request,
            Principal principal) {
        return service.create(request, actor(principal));
    }

    @PutMapping("/transactions/{id}")
    public FinanceTransactionResponse update(@PathVariable Long id,
            @Valid @RequestBody FinanceTransactionRequest request, Principal principal) {
        return service.update(id, request, actor(principal));
    }

    @PatchMapping("/transactions/{id}/void")
    public FinanceTransactionResponse voidTransaction(@PathVariable Long id, Principal principal) {
        return service.voidTransaction(id, actor(principal));
    }

    @PostMapping("/transactions/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceTransactionResponse payment(@PathVariable Long id, @Valid @RequestBody FinancePaymentRequest request,
            Principal principal) {
        return service.recordPayment(id, request, actor(principal));
    }

    @GetMapping("/due")
    public DueResponse due(@RequestParam(required = false) LocalDate date) {
        return service.due(date == null ? LocalDate.now() : date);
    }

    @GetMapping("/categories")
    public List<Map<String, String>> categories() {
        return service.categories();
    }

    private String actor(Principal principal) {
        return principal == null ? "system" : principal.getName();
    }
}