package com.project.fujicraft_management_system.RawMaterial;

import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialDto;
import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class RawMaterialController {
    private final RawMaterialService service;

    @GetMapping("/rawMaterials")
    public List<RawMaterialDto> list(@RequestParam(required = false) String name, @RequestParam(required = false) String category) {
        return service.list(name, category);
    }

    @PostMapping("/rawMaterials")
    @ResponseStatus(HttpStatus.CREATED)
    public RawMaterialDto create(@Valid @RequestBody RawMaterialRequest request) {
        return service.create(request);
    }

    @PutMapping("/rawMaterials/{id}")
    public RawMaterialDto update(@PathVariable Long id, @Valid @RequestBody RawMaterialRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/rawMaterials/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}