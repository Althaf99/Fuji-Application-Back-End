package com.project.fujicraft_management_system.Finance;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService {
    private static final String DEFAULT_CURRENCY = "LKR";
    private final FinanceTransactionRepository transactions;
    private final FinancePaymentRepository payments;
    private final FinanceAuditRepository audits;
    private final FinanceCategoryRepository categoryRepository;

    public FinanceService(FinanceTransactionRepository transactions, FinancePaymentRepository payments,
            FinanceAuditRepository audits, FinanceCategoryRepository categoryRepository) {
        this.transactions = transactions;
        this.payments = payments;
        this.audits = audits;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public FinanceTransactionResponse create(FinanceTransactionRequest request, String actor) {
        if (request.getInvoiceId() != null && transactions.existsByInvoiceIdAndTypeAndStatusNot(request.getInvoiceId(),
                request.getType(), FinanceStatus.VOIDED))
            throw conflict("A finance transaction already exists for this invoice");
        FinanceTransaction transaction = copy(new FinanceTransaction(), request);
        transaction.setCreatedBy(actor);
        transaction.setUpdatedBy(actor);
        FinanceTransaction saved = transactions.save(transaction);
        audit(saved.getId(), "CREATE", actor, "Transaction created");
        return response(saved);
    }

    @Transactional(readOnly = true)
    public FinancePageResponse list(LocalDate startDate, LocalDate endDate, FinanceTransactionType type,
            String category,
            FinanceStatus status, PaymentMethod paymentMethod, String partyName, String search,
            int page, int pageSize, String sortBy, String sortDirection) {
        validatePage(page, pageSize);
        DateRange range = range(startDate, endDate);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sort = List.of("id", "transactionDate", "dueDate", "amount", "status", "createdAt").contains(sortBy)
                ? sortBy
                : "transactionDate";
        var specification = FinanceSpecifications.between(range.start, range.end)
                .and(FinanceSpecifications.type(type)).and(FinanceSpecifications.category(category))
                .and(FinanceSpecifications.status(status)).and(FinanceSpecifications.paymentMethod(paymentMethod))
                .and(FinanceSpecifications.partyName(partyName)).and(FinanceSpecifications.search(search));
        var result = transactions.findAll(specification, PageRequest.of(page, pageSize, Sort.by(direction, sort)));
        return new FinancePageResponse(result.getContent().stream().map(this::response).toList(), page, pageSize,
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public FinanceTransactionResponse get(Long id) {
        return response(find(id));
    }

    @Transactional
    public FinanceTransactionResponse update(Long id, FinanceTransactionRequest request, String actor) {
        FinanceTransaction existing = find(id);
        if (existing.getStatus() == FinanceStatus.VOIDED)
            throw conflict("Voided transactions cannot be edited");
        if (request.getInvoiceId() != null && !request.getInvoiceId().equals(existing.getInvoiceId()) && transactions
                .existsByInvoiceIdAndTypeAndStatusNot(request.getInvoiceId(), request.getType(), FinanceStatus.VOIDED))
            throw conflict("A finance transaction already exists for this invoice");
        copy(existing, request);
        existing.setUpdatedBy(actor);
        FinanceTransaction saved = transactions.save(existing);
        audit(id, "UPDATE", actor, "Transaction updated");
        return response(saved);
    }

    @Transactional
    public FinanceTransactionResponse voidTransaction(Long id, String actor) {
        FinanceTransaction existing = find(id);
        if (existing.getStatus() == FinanceStatus.VOIDED)
            throw conflict("Transaction is already voided");
        existing.setStatus(FinanceStatus.VOIDED);
        existing.setVoidedAt(LocalDateTime.now());
        existing.setUpdatedBy(actor);
        FinanceTransaction saved = transactions.save(existing);
        audit(id, "VOID", actor, "Transaction voided");
        return response(saved);
    }

    @Transactional
    public FinanceTransactionResponse recordPayment(Long id, FinancePaymentRequest request, String actor) {
        FinanceTransaction transaction = find(id);
        if (transaction.getStatus() == FinanceStatus.VOIDED)
            throw conflict("Cannot pay a voided transaction");
        BigDecimal paid = payments.sumForTransaction(id);
        if (paid.add(request.getAmount()).compareTo(transaction.getAmount()) > 0)
            throw conflict("Payment exceeds transaction amount");
        if (request.getReferenceNumber() != null
                && payments.existsByTransactionIdAndReferenceNumber(id, request.getReferenceNumber()))
            throw conflict("Payment reference already exists for this transaction");
        FinancePayment payment = new FinancePayment();
        payment.setTransaction(transaction);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setCreatedBy(actor);
        payments.save(payment);
        BigDecimal totalPaid = paid.add(request.getAmount());
        if (totalPaid.compareTo(transaction.getAmount()) == 0)
            transaction.setStatus(transaction.getType() == FinanceTransactionType.INCOME ? FinanceStatus.RECEIVED
                    : FinanceStatus.PAID);
        else
            transaction.setStatus(FinanceStatus.OUTSTANDING);
        transaction.setUpdatedBy(actor);
        transactions.save(transaction);
        audit(id, "PAYMENT", actor, "Payment recorded: " + request.getAmount());
        return response(transaction);
    }

    @Transactional(readOnly = true)
    public DueResponse due(LocalDate today) {
        List<FinanceTransaction> values = transactions.findByDueDateBetweenAndStatusIn(LocalDate.of(1900, 1, 1),
                LocalDate.of(9999, 12, 31), List.of(FinanceStatus.OUTSTANDING, FinanceStatus.DUE));
        var overdue = values.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today))
                .map(this::response).toList();
        var todayItems = values.stream().filter(t -> today.equals(t.getDueDate())).map(this::response).toList();
        var upcoming = values.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isAfter(today))
                .map(this::response).toList();
        return new DueResponse(overdue, todayItems, upcoming);
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse summary(LocalDate startDate, LocalDate endDate, LocalDate comparisonStart,
            LocalDate comparisonEnd,
            FinanceTransactionType type, String category, FinanceStatus status, PaymentMethod method,
            String partyName) {
        LocalDate currentEnd = endDate == null ? LocalDate.now() : endDate;
        LocalDate currentStart = startDate == null ? currentEnd.withDayOfMonth(1) : startDate;
        DateRange currentRange = range(currentStart, currentEnd);
        long periodLength = ChronoUnit.DAYS.between(currentStart, currentEnd) + 1L;
        LocalDate defaultComparisonEnd = currentStart.minusDays(1);
        LocalDate defaultComparisonStart = defaultComparisonEnd.minusDays(periodLength - 1L);
        DateRange compareRange = range(comparisonStart == null ? defaultComparisonStart : comparisonStart,
                comparisonEnd == null ? defaultComparisonEnd : comparisonEnd);
        FinanceSummaryResponse result = new FinanceSummaryResponse();
        result.setCurrency(DEFAULT_CURRENCY);
        result.setPeriod(new FinanceSummaryResponse.Period(currentRange.start, currentRange.end));
        result.setComparisonPeriod(new FinanceSummaryResponse.Period(compareRange.start, compareRange.end));
        result.setCurrent(metrics(currentRange, type, category, status, method, partyName));
        result.setComparison(metrics(compareRange, type, category, status, method, partyName));
        result.setChanges(changes(result.getCurrent(), result.getComparison()));
        result.setExpenseBreakdown(expenseBreakdown(currentRange));
        return result;
    }

    public List<java.util.Map<String, String>> categories() {
        List<FinanceCategoryOption> options = categoryRepository.findByActiveTrueOrderByLabelAsc();
        if (!options.isEmpty()) {
            return options.stream().map(c -> Map.of("value", c.getValue(), "label", c.getLabel())).toList();
        }
        return List.of("SALES", "RAW_MATERIALS", "PAINT_ITEMS", "ELECTRICITY", "LABOR",
                "MACHINE_MAINTENANCE", "OVERHEAD", "DELIVERY").stream()
                .map(c -> Map.of("value", c, "label", label(c))).toList();
    }

    private FinanceSummaryResponse.Metrics metrics(DateRange range, FinanceTransactionType type,
            String category, FinanceStatus status, PaymentMethod method, String partyName) {
        var items = filtered(range, type, category, status, method, partyName);
        var metrics = new FinanceSummaryResponse.Metrics();
        BigDecimal revenue = sum(items, FinanceTransactionType.INCOME, true),
                expenses = sum(items, FinanceTransactionType.EXPENSE, true);
        metrics.setRevenue(revenue);
        metrics.setExpenses(expenses);
        metrics.setProfit(revenue.subtract(expenses));
        metrics.setProfitMargin(revenue.signum() == 0 ? BigDecimal.ZERO
                : metrics.getProfit().multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP));
        BigDecimal cashIn = payments.sumFilteredPayments(FinanceTransactionType.INCOME, FinanceStatus.VOIDED,
                range.start, range.end, DEFAULT_CURRENCY, category, status, partyName, method);
        BigDecimal cashOut = payments.sumFilteredPayments(FinanceTransactionType.EXPENSE, FinanceStatus.VOIDED,
                range.start, range.end, DEFAULT_CURRENCY, category, status, partyName, method);
        metrics.setCashIn(cashIn);
        metrics.setCashOut(cashOut);
        metrics.setCashFlow(cashIn.subtract(cashOut));
        metrics.setReceivables(items.stream()
                .filter(t -> t.getType() == FinanceTransactionType.INCOME
                        && (t.getStatus() == FinanceStatus.OUTSTANDING || t.getStatus() == FinanceStatus.DUE))
                .map(t -> t.getAmount().subtract(payments.sumForTransaction(t.getId())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        metrics.setInvoicesIssued(sum(items, FinanceTransactionType.INCOME, false));
        metrics.setPaymentsReceived(cashIn);
        return metrics;
    }

    private List<FinanceSummaryResponse.ExpenseBreakdown> expenseBreakdown(DateRange range) {
        var items = filtered(range, null, null, null, null, null);
        BigDecimal total = sum(items, FinanceTransactionType.EXPENSE, true);
        return categories().stream().map(c -> {
            String category = c.get("value");
            BigDecimal amount = items.stream()
                    .filter(t -> t.getType() == FinanceTransactionType.EXPENSE
                            && t.getCategory().equalsIgnoreCase(category)
                            && t.getStatus() != FinanceStatus.VOIDED)
                    .map(FinanceTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal percent = total.signum() == 0 ? BigDecimal.ZERO
                    : amount.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            return new FinanceSummaryResponse.ExpenseBreakdown(category, amount, percent);
        }).filter(e -> e.getAmount().signum() > 0).toList();
    }

    private FinanceSummaryResponse.Changes changes(FinanceSummaryResponse.Metrics current,
            FinanceSummaryResponse.Metrics previous) {
        var c = new FinanceSummaryResponse.Changes();
        c.setRevenuePercent(percent(current.getRevenue(), previous.getRevenue()));
        c.setExpensesPercent(percent(current.getExpenses(), previous.getExpenses()));
        c.setCashFlowPercent(percent(current.getCashFlow(), previous.getCashFlow()));
        c.setProfitMarginPercent(current.getProfitMargin().subtract(previous.getProfitMargin()));
        return c;
    }

    private BigDecimal percent(BigDecimal current, BigDecimal previous) {
        return previous.signum() == 0 ? BigDecimal.ZERO
                : current.subtract(previous).multiply(BigDecimal.valueOf(100)).divide(previous.abs(), 2,
                        RoundingMode.HALF_UP);
    }

    private List<FinanceTransaction> filtered(DateRange range, FinanceTransactionType type, String category,
            FinanceStatus status, PaymentMethod method, String partyName) {
        return transactions.findAll(FinanceSpecifications.between(range.start, range.end)
                .and(FinanceSpecifications.notVoided()).and(FinanceSpecifications.type(type))
                .and(FinanceSpecifications.category(category)).and(FinanceSpecifications.status(status))
                .and(FinanceSpecifications.paymentMethod(method)).and(FinanceSpecifications.partyName(partyName)));
    }

    private BigDecimal sum(List<FinanceTransaction> items, FinanceTransactionType type, boolean excludeVoided) {
        return items.stream()
                .filter(t -> t.getType() == type && (!excludeVoided || t.getStatus() != FinanceStatus.VOIDED))
                .map(FinanceTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FinanceTransaction find(Long id) {
        return transactions.findById(id).orElseThrow(
                () -> new FinanceModuleException(HttpStatus.NOT_FOUND, "Finance transaction not found: " + id));
    }

    private FinanceTransaction copy(FinanceTransaction t, FinanceTransactionRequest r) {
        t.setTransactionDate(r.getTransactionDate());
        t.setDueDate(r.getDueDate());
        t.setType(r.getType());
        t.setCategory(r.getCategory());
        t.setDescription(r.getDescription().trim());
        t.setReferenceNumber(r.getReferenceNumber().trim());
        t.setInvoiceId(r.getInvoiceId());
        t.setPurchaseOrderId(r.getPurchaseOrderId());
        t.setPartyName(r.getPartyName());
        t.setPaymentMethod(r.getPaymentMethod());
        t.setAmount(r.getAmount());
        String currency = r.getCurrency() == null || r.getCurrency().isBlank() ? DEFAULT_CURRENCY
                : r.getCurrency().trim().toUpperCase();
        if (!DEFAULT_CURRENCY.equals(currency))
            throw new FinanceModuleException(HttpStatus.BAD_REQUEST, "Unsupported currency: " + currency);
        t.setCurrency(currency);
        t.setStatus(r.getStatus());
        t.setNotes(r.getNotes());
        return t;
    }

    private FinanceTransactionResponse response(FinanceTransaction t) {
        return new FinanceTransactionResponse(t.getId(), t.getTransactionDate(), t.getDueDate(), t.getType(),
                t.getCategory(), t.getDescription(), t.getReferenceNumber(), t.getInvoiceId(), t.getPurchaseOrderId(),
                t.getPartyName(), t.getPaymentMethod(), t.getAmount(), t.getCurrency(), t.getStatus(), t.getNotes(),
                t.getCreatedBy());
    }

    private void audit(Long id, String action, String actor, String details) {
        FinanceAudit a = new FinanceAudit();
        a.setTransactionId(id);
        a.setAction(action);
        a.setActor(actor);
        a.setDetails(details);
        audits.save(a);
    }

    private DateRange range(LocalDate start, LocalDate end) {
        LocalDate s = start == null ? LocalDate.of(1900, 1, 1) : start, e = end == null ? LocalDate.now() : end;
        if (s.isAfter(e))
            throw new FinanceModuleException(HttpStatus.BAD_REQUEST, "startDate must not be after endDate");
        return new DateRange(s, e);
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > 200)
            throw new FinanceModuleException(HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and pageSize must be between 1 and 200");
    }

    private FinanceModuleException conflict(String message) {
        return new FinanceModuleException(HttpStatus.CONFLICT, message);
    }

    private String label(String value) {
        String[] words = value.replace('_', ' ').toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty())
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
        }
        return result.toString().trim();
    }

    private record DateRange(LocalDate start, LocalDate end) {
    }
}