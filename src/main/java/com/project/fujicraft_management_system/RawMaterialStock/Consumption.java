package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumption", uniqueConstraints = @UniqueConstraint(name = "uk_consumption_number", columnNames = "consumption_number"), indexes = {
        @Index(name = "idx_consumption_type_item", columnList = "item_type,item_id"),
        @Index(name = "idx_consumption_date", columnList = "date")
})
@Data
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "consumption_number", nullable = false)
    private String consumptionNumber;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;
    @Column(nullable = false)
    private String machineNo;
    @Column(nullable = false)
    private String moldNo;
    @Column(nullable = false)
    private String usedBy;
    @Column(columnDefinition = "text")
    private String notes;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @jakarta.persistence.PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @jakarta.persistence.PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}