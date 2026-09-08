package com.project.fujicraft_management_system.RawMaterial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RawMaterialGRNRepository extends JpaRepository<RawMaterialGRN, Long> {
    List<RawMaterialGRN> findByMaterialIdOrderByDateAsc(Long materialId);
    List<RawMaterialGRN> findByMaterialIdAndDateBetweenOrderByDateAsc(Long materialId, LocalDate startDate, LocalDate endDate);
    List<RawMaterialGRN> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);
}