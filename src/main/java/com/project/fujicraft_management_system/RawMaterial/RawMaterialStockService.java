package com.project.fujicraft_management_system.RawMaterial;

import com.project.fujicraft_management_system.RawMaterial.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialStockService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final RawMaterialRepository materialRepository;
    private final RawMaterialStockEntryRepository stockRepository;
    private final AuditLogEntryRepository auditRepository;

    @Transactional(readOnly = true)
    public List<RawMaterialStockEntryDto> list(Long materialId, String startDate, String endDate, String date) {
        LocalDate singleDate = date == null || date.isBlank() ? null : parseDate(date);
        LocalDate start = startDate == null || startDate.isBlank() ? null : parseDate(startDate);
        LocalDate end = endDate == null || endDate.isBlank() ? null : parseDate(endDate);
        if (start != null && end != null && start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate must not be after endDate");
        }
        List<RawMaterialStockEntry> entries;
        if (materialId != null && singleDate != null) {
            entries = stockRepository.findByMaterialIdAndDate(materialId, singleDate).stream().toList();
        } else if (materialId != null && start != null && end != null) {
            entries = stockRepository.findByMaterialIdAndDateBetweenOrderByDateAsc(materialId, start, end);
        } else if (materialId != null) {
            entries = stockRepository.findByMaterialIdOrderByDateAsc(materialId);
        } else if (singleDate != null) {
            entries = stockRepository.findByDateOrderByDateAsc(singleDate);
        } else if (start != null && end != null) {
            entries = stockRepository.findByDateBetweenOrderByDateAsc(start, end);
        } else {
            entries = stockRepository.findAll();
        }
        return entries.stream().map(this::toDto).toList();
    }

    @Transactional
    public RawMaterialStockEntryDto create(RawMaterialStockEntryRequest request, String user) {
        LocalDate date = parseDate(request.getDate());
        if (stockRepository.findByMaterialIdAndDate(request.getMaterialId(), date).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A stock entry already exists for this material and date");
        }
        RawMaterialStockEntry entry = new RawMaterialStockEntry();
        entry.setMaterial(material(request.getMaterialId()));
        entry.setDate(date);
        entry.setOpening(previousClosing(request.getMaterialId(), date));
        applyRequest(entry, request);
        Instant now = Instant.now();
        entry.setEnteredBy(user);
        entry.setEnteredAt(now);
        RawMaterialStockEntry saved = stockRepository.save(entry);
        recordAudit(saved, user, saved.isSubmitted() ? "SUBMIT" : "CREATE", describeCreate(saved));
        return toDto(saved);
    }

    @Transactional
    public RawMaterialStockEntryDto update(Long id, RawMaterialStockEntryRequest request, String user) {
        RawMaterialStockEntry entry = findEntry(id);
        if (entry.isSubmitted()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Submitted stock entries are locked and cannot be edited");
        }
        String oldValues = describe(entry);
        LocalDate date = parseDate(request.getDate());
        if (!entry.getMaterial().getId().equals(request.getMaterialId()) || !entry.getDate().equals(date)) {
            stockRepository.findByMaterialIdAndDate(request.getMaterialId(), date)
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(other -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "A stock entry already exists for this material and date"); });
        }
        entry.setMaterial(material(request.getMaterialId()));
        entry.setDate(date);
        applyRequest(entry, request);
        entry.setUpdatedBy(user);
        entry.setUpdatedAt(Instant.now());
        RawMaterialStockEntry saved = stockRepository.save(entry);
        recordAudit(saved, user, saved.isSubmitted() ? "SUBMIT" : "UPDATE", oldValues + " -> " + describe(saved));
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntryDto> auditTrail(Long id) {
        findEntry(id);
        return auditRepository.findByStockEntryIdOrderByTimestampAsc(id).stream().map(this::toAuditDto).toList();
    }

    @Transactional
    public void addReceivedFromGrn(Long materialId, LocalDate date, BigDecimal quantity, String user) {
        stockRepository.findByMaterialIdAndDate(materialId, date).ifPresent(entry -> {
            if (!entry.isSubmitted()) {
                String oldValues = describe(entry);
                entry.setReceived(entry.getReceived().add(quantity));
                entry.setClosing(entry.getOpening().add(entry.getReceived()).subtract(entry.getConsumed()));
                entry.setUpdatedBy(user);
                entry.setUpdatedAt(Instant.now());
                RawMaterialStockEntry saved = stockRepository.save(entry);
                recordAudit(saved, user, "UPDATE", oldValues + " -> " + describe(saved) + " (GRN received)");
            }
        });
    }

    private void applyRequest(RawMaterialStockEntry entry, RawMaterialStockEntryRequest request) {
        entry.setReceived(zeroIfNull(request.getReceived()));
        List<ConsumptionRequest> consumption = request.getConsumption() == null ? List.of() : request.getConsumption();
        BigDecimal consumed = consumption.isEmpty() ? zeroIfNull(request.getConsumed())
                : consumption.stream().map(ConsumptionRequest::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        entry.setConsumed(consumed);
        entry.setClosing(entry.getOpening().add(entry.getReceived()).subtract(consumed));
        entry.setSubmitted(request.isSubmitted());
        entry.getConsumption().clear();
        for (ConsumptionRequest item : consumption) {
            ConsumptionRecord record = new ConsumptionRecord();
            record.setStockEntry(entry);
            record.setMachine(item.getMachine());
            record.setMold(item.getMold());
            record.setShift(item.getShift());
            record.setQuantity(item.getQuantity());
            entry.getConsumption().add(record);
        }
    }

    private BigDecimal previousClosing(Long materialId, LocalDate date) {
        return stockRepository.findFirstByMaterialIdAndDateLessThanOrderByDateDesc(materialId, date)
                .map(RawMaterialStockEntry::getClosing).orElse(BigDecimal.ZERO);
    }

    private RawMaterial material(Long id) {
        return materialRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
    }

    private RawMaterialStockEntry findEntry(Long id) {
        return stockRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock entry not found"));
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates must use dd-MM-yyyy format");
        }
    }

    private void recordAudit(RawMaterialStockEntry entry, String user, String action, String changes) {
        AuditLogEntry audit = new AuditLogEntry();
        audit.setStockEntry(entry);
        audit.setUser(user);
        audit.setAction(action);
        audit.setTimestamp(Instant.now());
        audit.setChanges(changes);
        auditRepository.save(audit);
    }

    private RawMaterialStockEntryDto toDto(RawMaterialStockEntry entry) {
        RawMaterialStockEntryDto dto = new RawMaterialStockEntryDto();
        dto.setId(entry.getId());
        dto.setMaterialId(entry.getMaterial().getId());
        dto.setMaterialName(entry.getMaterial().getName());
        dto.setDate(entry.getDate());
        dto.setOpening(entry.getOpening());
        dto.setReceived(entry.getReceived());
        dto.setConsumed(entry.getConsumed());
        dto.setClosing(entry.getClosing());
        dto.setSubmitted(entry.isSubmitted());
        dto.setEnteredBy(entry.getEnteredBy());
        dto.setEnteredAt(entry.getEnteredAt());
        dto.setUpdatedBy(entry.getUpdatedBy());
        dto.setUpdatedAt(entry.getUpdatedAt());
        List<ConsumptionDto> consumption = new ArrayList<>();
        for (ConsumptionRecord record : entry.getConsumption()) {
            ConsumptionDto item = new ConsumptionDto();
            item.setId(record.getId());
            item.setMachine(record.getMachine());
            item.setMold(record.getMold());
            item.setShift(record.getShift());
            item.setQuantity(record.getQuantity());
            consumption.add(item);
        }
        dto.setConsumption(consumption);
        return dto;
    }

    private AuditLogEntryDto toAuditDto(AuditLogEntry audit) {
        AuditLogEntryDto dto = new AuditLogEntryDto();
        dto.setId(audit.getId());
        dto.setStockEntryId(audit.getStockEntry().getId());
        dto.setUser(audit.getUser());
        dto.setAction(audit.getAction());
        dto.setTimestamp(audit.getTimestamp());
        dto.setChanges(audit.getChanges());
        return dto;
    }

    private String describeCreate(RawMaterialStockEntry entry) {
        return "opening: " + entry.getOpening() + ", received: " + entry.getReceived() + ", consumed: " + entry.getConsumed()
                + ", closing: " + entry.getClosing() + ", submitted: " + entry.isSubmitted() + ", consumption records: " + entry.getConsumption().size();
    }

    private String describe(RawMaterialStockEntry entry) {
        return "materialId: " + entry.getMaterial().getId() + ", date: " + entry.getDate() + ", opening: " + entry.getOpening()
                + ", received: " + entry.getReceived() + ", consumed: " + entry.getConsumed() + ", closing: " + entry.getClosing()
                + ", submitted: " + entry.isSubmitted() + ", consumption records: " + entry.getConsumption().size();
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}