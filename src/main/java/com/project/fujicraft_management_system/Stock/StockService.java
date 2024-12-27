package com.project.fujicraft_management_system.Stock;

import com.project.fujicraft_management_system.DeliveryNote.dto.DeliveryNoteDto;
import com.project.fujicraft_management_system.Invoice.Invoice;
import com.project.fujicraft_management_system.Invoice.dto.Excess;
import com.project.fujicraft_management_system.Request.Request;
import com.project.fujicraft_management_system.Stock.Dto.Items;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Stock> getRequests(String itemName, String itemColor) {
        List<Stock> stocks = new ArrayList<Stock>();
        stockRepository.findAll(Specification.where(itemNameEquals(itemName)).and(itemColorEquals(itemColor))).forEach(updated -> stocks.add((Stock) updated));
        return stocks;
    }

    private Specification<Stock> itemNameEquals(final String itemName) {

        return StringUtils.isEmpty(itemName) ? null : (root, query, builder) -> builder.equal(root.get("itemName"), itemName);
    }

    private Specification<Stock> itemColorEquals(final String itemColor) {

        return StringUtils.isEmpty(itemColor) ? null : (root, query, builder) -> builder.equal(root.get("itemColor"), itemColor);
    }

    public ResponseEntity<Object> deleteStockById(int id) {
        try {
            //check if employee exist in database
            Optional<Stock> poRequest = stockRepository.findById(id);
            if (poRequest != null) {
                stockRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateStockById(int id, Stock stock) {
        Optional<Stock> poObj = stockRepository.findById(id);
        Stock poRequest = poObj.get();
        if (poObj != null) {
            poRequest.setItemName(stock.getItemName());
            poRequest.setQuantity(stock.getQuantity());
            poRequest.setItemColor(stock.getItemColor());
            return new ResponseEntity<>(stockRepository.save(poRequest), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public void updateStockBasedOnDeliveryNote(DeliveryNoteDto deliveryNoteDto) {
        deliveryNoteDto.getItems().forEach(e -> {
            e.getItem().forEach(i -> {
                Optional<Stock> stockOptional = stockRepository.findByItemNameAndItemColor(
                        e.getItemName(),
                        i.getItemColor()
                );
                if (stockOptional.isPresent()) {
                    Stock stock = stockOptional.get();
                    stock.setQuantity(stock.getQuantity() - i.getQuantity());
                    stockRepository.save(stock);
                }
            });
        });
    }
}
