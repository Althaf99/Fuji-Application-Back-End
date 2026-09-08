package com.project.fujicraft_management_system.RawMaterial;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "raw_material_audit_log")
@Data
public class AuditLogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_entry_id", nullable = false)
    private RawMaterialStockEntry stockEntry;

    private String user;
    private String action;
    private Instant timestamp;
    @Column(length = 4000)
    private String changes;
}