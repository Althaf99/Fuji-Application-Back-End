package com.project.fujicraft_management_system.RawMaterial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RawMaterialStockEntryRepository extends JpaRepository<RawMaterialStockEntry, Long> {
    Optional<RawMaterialStockEntry> findByMaterialIdAndDate(Long materialId, LocalDate date);
    Optional<RawMaterialStockEntry> findFirstByMaterialIdAndDateLessThanOrderByDateDesc(Long materialId, LocalDate date);
    List<RawMaterialStockEntry> findByMaterialIdOrderByDateAsc(Long materialId);
    List<RawMaterialStockEntry> findByMaterialIdAndDateBetweenOrderByDateAsc(Long materialId, LocalDate startDate, LocalDate endDate);
    List<RawMaterialStockEntry> findByDateOrderByDateAsc(LocalDate date);
    List<RawMaterialStockEntry> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);
}