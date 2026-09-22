package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Service
public class ConsumptionService {
    private final ConsumptionRepository consumptions;
    private final GrnItemRepository grnItems;
    private final GrnService grnService;

    public ConsumptionService(ConsumptionRepository consumptions, GrnItemRepository grnItems, GrnService grnService) {
        this.consumptions = consumptions;
        this.grnItems = grnItems;
        this.grnService = grnService;
    }

    @Transactional
    public Consumption create(ConsumptionRequest request) {
        grnService.lockItem(request.getItemType(), request.getItemId());
        validateStock(request.getItemType(), request.getItemId(), request.getQuantity(), BigDecimal.ZERO);
        return consumptions.save(copy(new Consumption(), request));
    }

    @Transactional(readOnly = true)
    public PageResponse<Consumption> list(int page, int size, ItemType type, Long itemId, LocalDate from,
            LocalDate to) {
        if (page < 0 || size < 1 || size > 200)
            throw new StockModuleException(HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and size must be between 1 and 200");
        LocalDate start = from == null ? LocalDate.of(1900, 1, 1) : from,
                end = to == null ? LocalDate.of(9999, 12, 31) : to;
        if (start.isAfter(end))
            throw new StockModuleException(HttpStatus.BAD_REQUEST, "from must not be after to");
        List<Consumption> values = consumptions.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "date"))
                .stream()
                .filter(c -> (type == null || c.getItemType() == type)
                        && (itemId == null || c.getItemId().equals(itemId)) && !c.getDate().isBefore(start)
                        && !c.getDate().isAfter(end))
                .toList();
        int fromIndex = Math.min(page * size, values.size()), toIndex = Math.min(fromIndex + size, values.size());
        return new PageResponse<>(values.subList(fromIndex, toIndex), page, size, values.size(),
                (values.size() + size - 1) / size);
    }

    @Transactional(readOnly = true)
    public Consumption get(Long id) {
        return consumptions.findById(id).orElseThrow(() -> VendorService.missing("Consumption", id));
    }

    @Transactional
    public Consumption update(Long id, ConsumptionRequest request) {
        Consumption old = get(id);
        grnService.lockItem(request.getItemType(), request.getItemId());
        if (old.getItemType() != request.getItemType() || !old.getItemId().equals(request.getItemId()))
            grnService.lockItem(old.getItemType(), old.getItemId());
        validateStock(request.getItemType(), request.getItemId(), request.getQuantity(),
                old.getItemType() == request.getItemType() && old.getItemId().equals(request.getItemId())
                        ? old.getQuantity()
                        : BigDecimal.ZERO);
        return consumptions.save(copy(old, request));
    }

    @Transactional
    public void delete(Long id) {
        Consumption old = get(id);
        grnService.lockItem(old.getItemType(), old.getItemId());
        consumptions.delete(old);
    }

    private void validateStock(ItemType type, Long id, BigDecimal requested, BigDecimal oldQuantity) {
        BigDecimal received = grnItems.sumQuantity(type, id, LocalDate.of(1900, 1, 1), LocalDate.of(9999, 12, 31));
        BigDecimal consumed = consumptions.sumQuantity(type, id, LocalDate.of(1900, 1, 1), LocalDate.of(9999, 12, 31));
        BigDecimal available = received.subtract(consumed).add(oldQuantity);
        if (requested.compareTo(available) > 0)
            throw new StockModuleException(HttpStatus.CONFLICT, "Consumption exceeds available stock: " + available);
    }

    private Consumption copy(Consumption item, ConsumptionRequest r) {
        item.setConsumptionNumber(
                r.getConsumptionNumber() == null || r.getConsumptionNumber().isBlank() ? "CON-" + UUID.randomUUID()
                        : r.getConsumptionNumber().trim());
        item.setDate(r.getDate());
        item.setItemType(r.getItemType());
        item.setItemId(r.getItemId());
        item.setQuantity(r.getQuantity());
        item.setMachineNo(r.getMachineNo().trim());
        item.setMoldNo(r.getMoldNo().trim());
        item.setUsedBy(r.getUsedBy().trim());
        item.setNotes(r.getNotes());
        return item;
    }
}