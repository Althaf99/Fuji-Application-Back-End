package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockQueryService {
    private final RawMaterialRepository rawMaterials;
    private final MasterBatchRepository masterBatches;
    private final GrnItemRepository grnItems;
    private final ConsumptionRepository consumptions;

    public StockQueryService(RawMaterialRepository rawMaterials, MasterBatchRepository masterBatches,
            GrnItemRepository grnItems, ConsumptionRepository consumptions) {
        this.rawMaterials = rawMaterials;
        this.masterBatches = masterBatches;
        this.grnItems = grnItems;
        this.consumptions = consumptions;
    }

    @Transactional(readOnly = true)
    public PageResponse<StockRow> list(ItemType type, Long itemId, Long vendorId, LocalDate from, LocalDate to,
            BigDecimal lowStock, int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > 200)
            throw new StockModuleException(HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and size must be between 1 and 200");
        LocalDate start = from == null ? LocalDate.of(1900, 1, 1) : from,
                end = to == null ? LocalDate.of(9999, 12, 31) : to;
        if (start.isAfter(end))
            throw new StockModuleException(HttpStatus.BAD_REQUEST, "from must not be after to");
        var rows = new ArrayList<StockRow>();
        if (type == null || type == ItemType.rawMaterial)
            rawMaterials.findAll().stream().filter(i -> itemId == null || i.getId().equals(itemId))
                    .filter(i -> vendorId == null || i.getVendor().getId().equals(vendorId))
                    .forEach(i -> add(rows, ItemType.rawMaterial, i.getId(), i.getName(), i.getCode(),
                            i.getVendor().getName(), start, end));
        if (type == null || type == ItemType.masterBatch)
            masterBatches.findAll().stream().filter(i -> itemId == null || i.getId().equals(itemId))
                    .filter(i -> vendorId == null || i.getVendor().getId().equals(vendorId))
                    .forEach(i -> add(rows, ItemType.masterBatch, i.getId(), i.getName(), i.getCode(),
                            i.getVendor().getName(), start, end));
        if (lowStock != null)
            rows.removeIf(row -> row.getAvailableQuantity().compareTo(lowStock) > 0);
        rows.sort((a, b) -> compare(a, b, sort, direction));
        int fromIndex = Math.min(page * size, rows.size()), toIndex = Math.min(fromIndex + size, rows.size());
        return new PageResponse<>(rows.subList(fromIndex, toIndex), page, size, rows.size(),
                (rows.size() + size - 1) / size);
    }

    @Transactional(readOnly = true)
    public StockRow get(ItemType type, Long id) {
        var result = list(type, id, null, null, null, null, 0, 1, "itemId", "asc");
        if (result.getContent().isEmpty())
            throw VendorService.missing("Stock item", id);
        return result.getContent().get(0);
    }

    private void add(List<StockRow> rows, ItemType type, Long id, String name, String code, String vendor,
            LocalDate from, LocalDate to) {
        var received = grnItems.sumQuantity(type, id, from, to);
        var consumed = consumptions.sumQuantity(type, id, from, to);
        rows.add(new StockRow(id, type, name, code, vendor, received, consumed, received.subtract(consumed),
                grnItems.lastReceivedDate(type, id), consumptions.lastConsumedDate(type, id)));
    }

    private int compare(StockRow a, StockRow b, String sort, String direction) {
        int result = switch (sort == null ? "itemId" : sort) {
            case "availableQuantity" -> a.getAvailableQuantity().compareTo(b.getAvailableQuantity());
            case "itemName" -> a.getItemName().compareToIgnoreCase(b.getItemName());
            case "itemCode" -> a.getItemCode().compareToIgnoreCase(b.getItemCode());
            default -> a.getItemId().compareTo(b.getItemId());
        };
        return "desc".equalsIgnoreCase(direction) ? -result : result;
    }
}