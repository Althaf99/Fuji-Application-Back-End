package com.project.fujicraft_management_system.RawMaterial;

import com.project.fujicraft_management_system.RawMaterial.dto.AuditLogEntryDto;
import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialStockEntryDto;
import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialStockEntryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class RawMaterialStockController {
    private final RawMaterialStockService service;

    @GetMapping("/rawMaterialStock")
    public List<RawMaterialStockEntryDto> list(@RequestParam(required = false) Long materialId,
                                                @RequestParam(required = false) String startDate,
                                                @RequestParam(required = false) String endDate,
                                                @RequestParam(required = false) String date) {
        return service.list(materialId, startDate, endDate, date);
    }

    @PostMapping("/rawMaterialStock")
    @ResponseStatus(HttpStatus.CREATED)
    public RawMaterialStockEntryDto create(@Valid @RequestBody RawMaterialStockEntryRequest request,
                                           @RequestHeader(value = "X-User", defaultValue = "system") String user) {
        return service.create(request, user);
    }

    @PutMapping("/rawMaterialStock/{id}")
    public RawMaterialStockEntryDto update(@PathVariable Long id, @Valid @RequestBody RawMaterialStockEntryRequest request,
                                           @RequestHeader(value = "X-User", defaultValue = "system") String user) {
        return service.update(id, request, user);
    }

    @GetMapping("/rawMaterialStock/{id}/auditTrail")
    public List<AuditLogEntryDto> auditTrail(@PathVariable Long id) {
        return service.auditTrail(id);
    }
}