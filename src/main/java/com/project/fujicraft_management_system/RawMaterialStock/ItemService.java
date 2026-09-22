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
    private final VendorRepository vendors;
    private final GrnItemRepository grnItems;
    private final ConsumptionRepository consumptions;

    public ItemService(RawMaterialRepository rawMaterials, MasterBatchRepository masterBatches,
            VendorRepository vendors,
            GrnItemRepository grnItems, ConsumptionRepository consumptions) {
        this.rawMaterials = rawMaterials;
        this.masterBatches = masterBatches;
        this.vendors = vendors;
        this.grnItems = grnItems;
        this.consumptions = consumptions;
    }

    @Transactional
    public RawMaterial createRawMaterial(ItemRequest r) {
        return rawMaterials.save(copy(new RawMaterial(), r));
    }

    @Transactional
    public MasterBatch createMasterBatch(ItemRequest r) {
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
        return rawMaterials.save(copy(getRawMaterial(id), r));
    }

    @Transactional
    public MasterBatch updateMasterBatch(Long id, ItemRequest r) {
        return masterBatches.save(copy(getMasterBatch(id), r));
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
    public PageResponse<RawMaterial> listRawMaterials(int page, int size, String sort, String direction, String search,
            Long vendorId) {
        var values = rawMaterials.findAll().stream().filter(item -> matches(item.getName(), item.getCode(), search)
                && (vendorId == null || item.getVendor().getId().equals(vendorId))).toList();
        return page(values, page, size, sort, direction);
    }

    @Transactional(readOnly = true)
    public PageResponse<MasterBatch> listMasterBatches(int page, int size, String sort, String direction, String search,
            Long vendorId) {
        var values = masterBatches.findAll().stream().filter(item -> matches(item.getName(), item.getCode(), search)
                && (vendorId == null || item.getVendor().getId().equals(vendorId))).toList();
        return page(values, page, size, sort, direction);
    }

    private void ensureUnused(ItemType type, Long id) {
        if (grnItems.existsByItemTypeAndItemId(type, id) || consumptions.existsByItemTypeAndItemId(type, id))
            throw new StockModuleException(HttpStatus.CONFLICT, "Item is referenced by stock transactions");
    }

    private RawMaterial copy(RawMaterial item, ItemRequest r) {
        item.setName(r.getName().trim());
        item.setCode(r.getCode().trim());
        item.setType(r.getType().trim());
        item.setColor(r.getColor());
        item.setSize(r.getSize());
        item.setPrice(r.getPrice());
        item.setVendor(vendor(r.getVendorId()));
        return item;
    }

    private MasterBatch copy(MasterBatch item, ItemRequest r) {
        item.setName(r.getName().trim());
        item.setCode(r.getCode().trim());
        item.setType(r.getType().trim());
        item.setColor(r.getColor());
        item.setSize(r.getSize());
        item.setPrice(r.getPrice());
        item.setVendor(vendor(r.getVendorId()));
        return item;
    }

    private Vendor vendor(Long id) {
        return vendors.findById(id).orElseThrow(() -> VendorService.missing("Vendor", id));
    }

    private boolean matches(String name, String code, String search) {
        return search == null || search.isBlank()
                || name.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))
                || code.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
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
        return switch (List.of("name", "code", "createdAt", "updatedAt").contains(field) ? field : "id") {
            case "name" -> value instanceof RawMaterial r ? r.getName() : ((MasterBatch) value).getName();
            case "code" -> value instanceof RawMaterial r ? r.getCode() : ((MasterBatch) value).getCode();
            case "createdAt" ->
                value instanceof RawMaterial r ? r.getCreatedAt() : ((MasterBatch) value).getCreatedAt();
            case "updatedAt" ->
                value instanceof RawMaterial r ? r.getUpdatedAt() : ((MasterBatch) value).getUpdatedAt();
            default -> value instanceof RawMaterial r ? r.getId() : ((MasterBatch) value).getId();
        };
    }
}