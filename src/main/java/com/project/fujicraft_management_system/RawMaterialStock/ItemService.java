package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ItemService {
    private final RawMaterialRepository rawMaterials;
    private final MasterBatchRepository masterBatches;
    private final GrnItemRepository grnItems;
    private final ConsumptionRepository consumptions;

    public ItemService(RawMaterialRepository rawMaterials, MasterBatchRepository masterBatches,
            GrnItemRepository grnItems, ConsumptionRepository consumptions) {
        this.rawMaterials = rawMaterials;
        this.masterBatches = masterBatches;
        this.grnItems = grnItems;
        this.consumptions = consumptions;
    }

    @Transactional
    public RawMaterial createRawMaterial(ItemRequest r) {
        String code = normalizeCode(r.getCode());
        if (rawMaterials.existsByCodeIgnoreCase(code)) {
            throw new StockModuleException(HttpStatus.CONFLICT, "Raw material code already exists: " + code);
        }
        return rawMaterials.save(copy(new RawMaterial(), r));
    }

    @Transactional
    public MasterBatch createMasterBatch(ItemRequest r) {
        String code = normalizeCode(r.getCode());
        if (masterBatches.existsByCodeIgnoreCase(code)) {
            throw new StockModuleException(HttpStatus.CONFLICT, "Master batch code already exists: " + code);
        }
        return masterBatches.save(copy(new MasterBatch(), r));
    }

    @Transactional(readOnly = true)
    public RawMaterial getRawMaterial(Long id) {
        return rawMaterials.findById(id).orElseThrow(() -> VendorService.missing("Raw material", id));
    }

    @Transactional(readOnly = true)
    public MasterBatch getMasterBatch(Long id) {
        return masterBatches.findById(id).orElseThrow(() -> VendorService.missing("Master batch", id));
    }

    @Transactional
    public RawMaterial updateRawMaterial(Long id, ItemRequest r) {
        RawMaterial existing = getRawMaterial(id);
        String code = normalizeCode(r.getCode());
        if (!code.equalsIgnoreCase(existing.getCode()) && rawMaterials.existsByCodeIgnoreCase(code)) {
            throw new StockModuleException(HttpStatus.CONFLICT, "Raw material code already exists: " + code);
        }
        return rawMaterials.save(copy(existing, r));
    }

    @Transactional
    public MasterBatch updateMasterBatch(Long id, ItemRequest r) {
        MasterBatch existing = getMasterBatch(id);
        String code = normalizeCode(r.getCode());
        if (!code.equalsIgnoreCase(existing.getCode()) && masterBatches.existsByCodeIgnoreCase(code)) {
            throw new StockModuleException(HttpStatus.CONFLICT, "Master batch code already exists: " + code);
        }
        return masterBatches.save(copy(existing, r));
    }

    @Transactional
    public void deleteRawMaterial(Long id) {
        getRawMaterial(id);
        ensureUnused(ItemType.rawMaterial, id);
        rawMaterials.deleteById(id);
    }

    @Transactional
    public void deleteMasterBatch(Long id) {
        getMasterBatch(id);
        ensureUnused(ItemType.masterBatch, id);
        masterBatches.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<RawMaterial> listRawMaterials(int page, int size, String sort, String direction,
            String search) {
        var values = rawMaterials.findAll().stream().filter(item -> matches(item.getCode(), search)).toList();
        return page(values, page, size, sort, direction);
    }

    @Transactional(readOnly = true)
    public PageResponse<MasterBatch> listMasterBatches(int page, int size, String sort, String direction,
            String search) {
        var values = masterBatches.findAll().stream().filter(item -> matches(item.getCode(), search)).toList();
        return page(values, page, size, sort, direction);
    }

    private void ensureUnused(ItemType type, Long id) {
        if (grnItems.existsByItemTypeAndItemId(type, id) || consumptions.existsByItemTypeAndItemId(type, id))
            throw new StockModuleException(HttpStatus.CONFLICT, "Item is referenced by stock transactions");
    }

    private RawMaterial copy(RawMaterial item, ItemRequest r) {
        item.setCode(normalizeCode(r.getCode()));
        item.setType(r.getType().trim());
        item.setColor(r.getColor());
        item.setPrice(r.getPrice());
        return item;
    }

    private MasterBatch copy(MasterBatch item, ItemRequest r) {
        item.setCode(normalizeCode(r.getCode()));
        item.setType(r.getType().trim());
        item.setColor(r.getColor());
        item.setPrice(r.getPrice());
        return item;
    }

    private boolean matches(String code, String search) {
        return search == null || search.isBlank()
                || code.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private String normalizeCode(String code) {
        return code.trim();
    }

    private <T> PageResponse<T> page(List<T> source, int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > 200)
            throw new StockModuleException(HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and size must be between 1 and 200");
        Comparator<T> comparator = Comparator.comparing(value -> (Comparable<Object>) property(value, sort),
                Comparator.nullsLast(Comparator.naturalOrder()));
        if ("desc".equalsIgnoreCase(direction))
            comparator = comparator.reversed();
        var sorted = source.stream().sorted(comparator).toList();
        int from = Math.min(page * size, sorted.size());
        int to = Math.min(from + size, sorted.size());
        return new PageResponse<>(sorted.subList(from, to), page, size, sorted.size(),
                (sorted.size() + size - 1) / size);
    }

    private Object property(Object value, String field) {
        return switch (List.of("code", "createdAt", "updatedAt").contains(field) ? field : "id") {
            case "code" -> value instanceof RawMaterial r ? r.getCode() : ((MasterBatch) value).getCode();
            case "createdAt" ->
                value instanceof RawMaterial r ? r.getCreatedAt() : ((MasterBatch) value).getCreatedAt();
            case "updatedAt" ->
                value instanceof RawMaterial r ? r.getUpdatedAt() : ((MasterBatch) value).getUpdatedAt();
            default -> value instanceof RawMaterial r ? r.getId() : ((MasterBatch) value).getId();
        };
    }
}