package com.project.fujicraft_management_system.RawMaterial.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AuditLogEntryDto {
    private Long id;
    private Long stockEntryId;
    private String user;
    private String action;
    private Instant timestamp;
    private String changes;
}