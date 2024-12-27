package com.project.fujicraft_management_system.Stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock,Integer>, JpaSpecificationExecutor<Stock> {
    Optional<Stock> findByItemNameAndItemColor(String itemName, String itemColor);
}
