package com.project.fujicraft_management_system.Stock;

import com.project.fujicraft_management_system.Stock.Dto.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
