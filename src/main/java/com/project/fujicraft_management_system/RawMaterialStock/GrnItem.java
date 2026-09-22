package com.project.fujicraft_management_system.RawMaterialStock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grn_item", indexes = {
        @Index(name = "idx_grn_item_type_item", columnList = "item_type,item_id"),
        @Index(name = "idx_grn_item_grn", columnList = "grn_id")
})
@Data
public class GrnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grn_id", nullable = false)
    @JsonIgnore
    private Grn grn;
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;
    @Column(precision = 19, scale = 6)
    private BigDecimal unitPrice;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}