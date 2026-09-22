package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/consumptions")
public class ConsumptionController {
    private final ConsumptionService service;

    public ConsumptionController(ConsumptionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consumption create(@Valid @RequestBody ConsumptionRequest request) {
        return service.create(request);
    }

    @GetMapping
    public PageResponse<Consumption> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) ItemType itemType,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(page, size, itemType, itemId, from, to);
    }

    @GetMapping("/{id}")
    public Consumption get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Consumption update(@PathVariable Long id, @Valid @RequestBody ConsumptionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}