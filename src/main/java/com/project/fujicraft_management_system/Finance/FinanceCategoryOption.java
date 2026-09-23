package com.project.fujicraft_management_system.Finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "finance_categories")
@Data
public class FinanceCategoryOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String value;
    @Column(nullable = false, length = 120)
    private String label;
    @Column(nullable = false)
    private boolean active = true;
}