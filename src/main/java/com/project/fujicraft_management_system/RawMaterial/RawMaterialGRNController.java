package com.project.fujicraft_management_system.RawMaterial;

import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialGRNDto;
import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialGRNRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class RawMaterialGRNController {
    private final RawMaterialGRNService service;

    @GetMapping("/rawMaterialGRN")
    public List<RawMaterialGRNDto> list(@RequestParam(required = false) Long materialId,
                                        @RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        return service.list(materialId, startDate, endDate);
    }

    @PostMapping("/rawMaterialGRN")
    @ResponseStatus(HttpStatus.CREATED)
    public RawMaterialGRNDto create(@Valid @RequestBody RawMaterialGRNRequest request,
                                    @RequestHeader(value = "X-User", defaultValue = "system") String user) {
        return service.create(request, user);
    }
}