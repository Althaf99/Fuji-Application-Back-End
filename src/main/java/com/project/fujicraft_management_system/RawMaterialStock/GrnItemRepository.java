package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GrnItemRepository extends JpaRepository<GrnItem, Long> {
    boolean existsByItemTypeAndItemId(ItemType itemType, Long itemId);

    @Query("select coalesce(sum(i.quantity), 0) from GrnItem i where i.itemType = :type and i.itemId = :itemId and i.grn.date between :from and :to")
    BigDecimal sumQuantity(@Param("type") ItemType type, @Param("itemId") Long itemId, @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("select max(i.grn.date) from GrnItem i where i.itemType = :type and i.itemId = :itemId")
    LocalDate lastReceivedDate(@Param("type") ItemType type, @Param("itemId") Long itemId);
}