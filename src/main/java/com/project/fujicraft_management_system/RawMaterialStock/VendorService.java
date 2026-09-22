package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendorService {
    private final VendorRepository vendorRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final MasterBatchRepository masterBatchRepository;
    private final GrnRepository grnRepository;

    public VendorService(VendorRepository vendorRepository, RawMaterialRepository rawMaterialRepository,
            MasterBatchRepository masterBatchRepository, GrnRepository grnRepository) {
        this.vendorRepository = vendorRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.masterBatchRepository = masterBatchRepository;
        this.grnRepository = grnRepository;
    }

    @Transactional
    public Vendor create(VendorRequest request) {
        return vendorRepository.save(copy(new Vendor(), request));
    }

    @Transactional(readOnly = true)
    public PageResponse<Vendor> list(int page, int size, String sort, String direction) {
        var result = vendorRepository.findAll(PageRequest.of(page, size, safeSort(sort, direction)));
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Vendor get(Long id) {
        return vendorRepository.findById(id).orElseThrow(() -> missing("Vendor", id));
    }

    @Transactional
    public Vendor update(Long id, VendorRequest request) {
        return vendorRepository.save(copy(get(id), request));
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        if (rawMaterialRepository.existsByVendorId(id) || masterBatchRepository.existsByVendorId(id)
                || grnRepository.existsByVendorId(id))
            throw new StockModuleException(HttpStatus.CONFLICT,
                    "Vendor is referenced by raw materials, master batches, or GRNs");
        vendorRepository.deleteById(id);
    }

    private Vendor copy(Vendor vendor, VendorRequest request) {
        vendor.setName(request.getName().trim());
        vendor.setLocation(request.getLocation());
        vendor.setContactPerson(request.getContactPerson());
        vendor.setContactNumber(request.getContactNumber());
        vendor.setBankName(request.getBankName());
        vendor.setBankAccountNumber(request.getBankAccountNumber());
        return vendor;
    }

    static StockModuleException missing(String name, Long id) {
        return new StockModuleException(HttpStatus.NOT_FOUND, name + " not found: " + id);
    }

    static Sort safeSort(String sort, String direction) {
        var field = List.of("id", "name", "code", "createdAt", "updatedAt").contains(sort) ? sort : "id";
        var dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, field);
    }
}