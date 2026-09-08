package com.project.fujicraft_management_system.RawMaterial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    Optional<RawMaterial> findByCode(String code);
    List<RawMaterial> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(String name, String category);
    List<RawMaterial> findByNameContainingIgnoreCase(String name);
    List<RawMaterial> findByCategoryIgnoreCase(String category);
}