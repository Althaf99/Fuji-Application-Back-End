package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;
import java.time.LocalDate;

@Service
public class GrnService {
    private final GrnRepository grns;
    private final VendorRepository vendors;
    private final RawMaterialRepository rawMaterials;
    private final MasterBatchRepository masterBatches;

    public GrnService(GrnRepository grns, VendorRepository vendors, RawMaterialRepository rawMaterials,
            MasterBatchRepository masterBatches) {
        this.grns = grns;
        this.vendors = vendors;
        this.rawMaterials = rawMaterials;
        this.masterBatches = masterBatches;
    }

    @Transactional
    public Grn create(GrnRequest request) {
        return grns.save(populate(new Grn(), request, true));
    }

    @Transactional(readOnly = true)
    public PageResponse<Grn> list(int page, int size, String sort, String direction, Long vendorId, LocalDate from,
            LocalDate to) {
        validatePage(page, size);
        String field = java.util.List.of("id", "grnNumber", "date", "createdAt", "updatedAt").contains(sort) ? sort
                : "date";
        LocalDate start = from == null ? LocalDate.of(1900, 1, 1) : from,
                end = to == null ? LocalDate.of(9999, 12, 31) : to;
        if (start.isAfter(end))
            throw new StockModuleException(HttpStatus.BAD_REQUEST, "from must not be after to");
        var dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        var values = grns.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.by(dir, field))).getContent().stream()
                .filter(grn -> (vendorId == null || grn.getVendor().getId().equals(vendorId))
                        && !grn.getDate().isBefore(start) && !grn.getDate().isAfter(end))
                .toList();
        int fromIndex = Math.min(page * size, values.size()), toIndex = Math.min(fromIndex + size, values.size());
        return new PageResponse<>(values.subList(fromIndex, toIndex), page, size, values.size(),
                (values.size() + size - 1) / size);
    }

    @Transactional(readOnly = true)
    public Grn get(Long id) {
        return grns.findById(id).orElseThrow(() -> VendorService.missing("GRN", id));
    }

    @Transactional
    public Grn update(Long id, GrnRequest request) {
        Grn grn = get(id);
        lockItems(grn);
        grn.getItems().clear();
        return grns.save(populate(grn, request, false));
    }

    @Transactional
    public void delete(Long id) {
        Grn grn = get(id);
        lockItems(grn);
        grns.delete(grn);
    }

    private Grn populate(Grn grn, GrnRequest request, boolean generateNumber) {
        if (request.getGrnNumber() == null || request.getGrnNumber().isBlank()) {
            if (generateNumber)
                grn.setGrnNumber("GRN-" + UUID.randomUUID());
        } else {
            grn.setGrnNumber(request.getGrnNumber().trim());
        }
        if (grn.getGrnNumber() == null || grn.getGrnNumber().isBlank())
            throw new StockModuleException(HttpStatus.BAD_REQUEST, "grnNumber is required");
        grn.setDate(request.getDate());
        grn.setVendor(vendors.findById(request.getVendorId())
                .orElseThrow(() -> VendorService.missing("Vendor", request.getVendorId())));
        grn.setPurchaseOrderId(request.getPurchaseOrderId());
        grn.setNotes(request.getNotes());
        var ids = new HashSet<String>();
        for (GrnItemRequest itemRequest : request.getItems()) {
            lockItem(itemRequest.getItemType(), itemRequest.getItemId());
            if (!ids.add(itemRequest.getItemType() + ":" + itemRequest.getItemId()))
                throw new StockModuleException(HttpStatus.BAD_REQUEST, "Duplicate GRN item");
            var item = new GrnItem();
            item.setGrn(grn);
            item.setItemType(itemRequest.getItemType());
            item.setItemId(itemRequest.getItemId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            grn.getItems().add(item);
        }
        return grn;
    }

    private void lockItems(Grn grn) {
        grn.getItems().forEach(item -> lockItem(item.getItemType(), item.getItemId()));
    }

    void lockItem(ItemType type, Long id) {
        if (type == ItemType.rawMaterial)
            rawMaterials.findByIdForUpdate(id).orElseThrow(() -> VendorService.missing("Raw material", id));
        else
            masterBatches.findByIdForUpdate(id).orElseThrow(() -> VendorService.missing("Master batch", id));
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > 200)
            throw new StockModuleException(HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and size must be between 1 and 200");
    }
}