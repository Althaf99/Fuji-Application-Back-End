package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/raw-material-stock")
@CrossOrigin(origins = "*")
public class StockQueryController {
    private final StockQueryService service;

    public StockQueryController(StockQueryService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<StockRow> list(@RequestParam(required = false) ItemType itemType,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) BigDecimal lowStock, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "itemId") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return service.list(itemType, itemId, from, to, lowStock, page, size, sort, direction);
    }

    @GetMapping("/{itemType}/{itemId}")
    public StockRow get(@PathVariable ItemType itemType, @PathVariable Long itemId) {
        return service.get(itemType, itemId);
    }
}