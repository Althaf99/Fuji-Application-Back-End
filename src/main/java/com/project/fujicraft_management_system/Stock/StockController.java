package com.project.fujicraft_management_system.Stock;

import com.project.fujicraft_management_system.Request.Request;
import com.project.fujicraft_management_system.Stock.Dto.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class StockController {

    @Autowired
    StockService stockService;

    @PostMapping("/stock")
    public void updateStock(@RequestBody List<Items> stockRequests) {
        stockService.updateStock(stockRequests);
    }

    @GetMapping("/stock")
    private List<Stock> getRequest(@RequestParam(required = false) String itemName, @RequestParam(required = false) String itemColor){
        return stockService.getRequests( itemName, itemColor);
    }

    @DeleteMapping("/stock/{id}")
    private ResponseEntity<Object> deleteStock(@PathVariable("id") int id){
        return stockService.deleteStockById(id);
    }

    @PutMapping("/stock/{id}")
    private ResponseEntity<Object> updateStockById(@PathVariable int id, @RequestBody Stock stock){
        return stockService.updateStockById(id,stock);
    }

}
