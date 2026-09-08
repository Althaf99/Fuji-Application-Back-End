package com.project.fujicraft_management_system.RawMaterial;

import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialDto;
import com.project.fujicraft_management_system.RawMaterial.dto.RawMaterialRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialService {
    private final RawMaterialRepository repository;

    public List<RawMaterialDto> list(String name, String category) {
        String safeName = name == null ? "" : name;
        List<RawMaterial> materials;
        if (category != null && !category.isBlank()) {
            materials = safeName.isBlank() ? repository.findByCategoryIgnoreCase(category)
                    : repository.findByNameContainingIgnoreCaseAndCategoryIgnoreCase(safeName, category);
        } else {
            materials = repository.findByNameContainingIgnoreCase(safeName);
        }
        return materials.stream().map(this::toDto).toList();
    }

    public RawMaterialDto create(RawMaterialRequest request) {
        repository.findByCode(request.getCode()).ifPresent(material -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Material code already exists");
        });
        return toDto(repository.save(populate(new RawMaterial(), request)));
    }

    public RawMaterialDto update(Long id, RawMaterialRequest request) {
        RawMaterial material = get(id);
        repository.findByCode(request.getCode()).filter(other -> !other.getId().equals(id)).ifPresent(other -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Material code already exists");
        });
        return toDto(repository.save(populate(material, request)));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    public RawMaterial get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
    }

    private RawMaterial populate(RawMaterial material, RawMaterialRequest request) {
        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setCategory(request.getCategory());
        material.setUnit(request.getUnit() == null || request.getUnit().isBlank() ? "kg" : request.getUnit());
        material.setReorderLevel(request.getReorderLevel());
        material.setReorderQuantity(request.getReorderQuantity());
        material.setDosageRatio(request.getDosageRatio());
        return material;
    }

    private RawMaterialDto toDto(RawMaterial material) {
        RawMaterialDto dto = new RawMaterialDto();
        dto.setId(material.getId());
        dto.setCode(material.getCode());
        dto.setName(material.getName());
        dto.setCategory(material.getCategory());
        dto.setUnit(material.getUnit());
        dto.setReorderLevel(material.getReorderLevel());
        dto.setReorderQuantity(material.getReorderQuantity());
        dto.setDosageRatio(material.getDosageRatio());
        dto.setCreatedAt(material.getCreatedAt());
        dto.setUpdatedAt(material.getUpdatedAt());
        return dto;
    }
}