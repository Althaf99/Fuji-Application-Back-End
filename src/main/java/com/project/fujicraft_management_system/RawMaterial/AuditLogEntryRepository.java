package com.project.fujicraft_management_system.RawMaterial;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, Long> {
    List<AuditLogEntry> findByStockEntryIdOrderByTimestampAsc(Long stockEntryId);
}