package com.project.fujicraft_management_system.RawMaterial;

import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialGRNDto;
import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialGRNRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialGRNService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final RawMaterialRepository materialRepository;
    private final RawMaterialGRNRepository grnRepository;
    private final RawMaterialStockService stockService;

    @Transactional(readOnly = true)
    public List<RawMaterialGRNDto> list(Long materialId, String startDate, String endDate) {
        LocalDate start = startDate == null || startDate.isBlank() ? null : parseDate(startDate);
        LocalDate end = endDate == null || endDate.isBlank() ? null : parseDate(endDate);
        if (start != null && end != null && start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate must not be after endDate");
        }
        List<RawMaterialGRN> grns;
        if (materialId != null && start != null && end != null) {
            grns = grnRepository.findByMaterialIdAndDateBetweenOrderByDateAsc(materialId, start, end);
        } else if (materialId != null) {
            grns = grnRepository.findByMaterialIdOrderByDateAsc(materialId);
        } else if (start != null && end != null) {
            grns = grnRepository.findByDateBetweenOrderByDateAsc(start, end);
        } else {
            grns = grnRepository.findAll();
        }
        return grns.stream().map(this::toDto).toList();
    }

    @Transactional
    public RawMaterialGRNDto create(RawMaterialGRNRequest request, String user) {
        LocalDate date = parseDate(request.getDate());
        RawMaterial material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
        RawMaterialGRN grn = new RawMaterialGRN();
        grn.setMaterial(material);
        grn.setSupplier(request.getSupplier());
        grn.setInvoiceNo(request.getInvoiceNo());
        grn.setQuantity(request.getQuantity());
        grn.setDate(date);
        grn.setCreatedBy(user);
        grn.setCreatedAt(Instant.now());
        RawMaterialGRN saved = grnRepository.save(grn);
        stockService.addReceivedFromGrn(material.getId(), date, saved.getQuantity(), user);
        return toDto(saved);
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates must use dd-MM-yyyy format");
        }
    }

    private RawMaterialGRNDto toDto(RawMaterialGRN grn) {
        RawMaterialGRNDto dto = new RawMaterialGRNDto();
        dto.setId(grn.getId());
        dto.setMaterialId(grn.getMaterial().getId());
        dto.setSupplier(grn.getSupplier());
        dto.setInvoiceNo(grn.getInvoiceNo());
        dto.setQuantity(grn.getQuantity());
        dto.setDate(grn.getDate());
        dto.setCreatedBy(grn.getCreatedBy());
        dto.setCreatedAt(grn.getCreatedAt());
        return dto;
    }
}