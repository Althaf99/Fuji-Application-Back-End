package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    boolean existsByItemTypeAndItemId(ItemType itemType, Long itemId);

    @Query("select coalesce(sum(c.quantity), 0) from Consumption c where c.itemType = :type and c.itemId = :itemId and c.date between :from and :to")
    BigDecimal sumQuantity(@Param("type") ItemType type, @Param("itemId") Long itemId, @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("select max(c.date) from Consumption c where c.itemType = :type and c.itemId = :itemId")
    LocalDate lastConsumedDate(@Param("type") ItemType type, @Param("itemId") Long itemId);
}