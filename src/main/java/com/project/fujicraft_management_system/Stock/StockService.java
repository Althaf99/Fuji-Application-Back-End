package com.project.fujicraft_management_system.Stock;

import com.project.fujicraft_management_system.Stock.Dto.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;
    public void updateStock(List<Items> stockRequests) {
        for (Items request : stockRequests) {
            for (Items.Item item : request.getItem()) {
                stockRepository.findByItemNameAndItemColor(request.getItemName(), item.getItemColor())
                        .ifPresentOrElse(
                                existingStock -> {
                                    // Update the quantity
                                    existingStock.setQuantity(existingStock.getQuantity() + Integer.parseInt(item.getQuantity()));
                                    stockRepository.save(existingStock);
                                },
                                () -> {
                                    // Insert a new entry
                                    Stock newStock = new Stock();
                                    newStock.setItemName(request.getItemName());
                                    newStock.setItemColor(item.getItemColor());
                                    newStock.setQuantity(Integer.parseInt(item.getQuantity()));
                                    stockRepository.save(newStock);
                                }
                        );
            }
        }
    }
}
