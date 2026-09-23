package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/grns")
@CrossOrigin(origins = "*")

public class GrnController {
    private final GrnService service;

    public GrnController(GrnService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Grn create(@Valid @RequestBody GrnRequest request) {
        return service.create(request);
    }

    @GetMapping
    public PageResponse<Grn> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction, @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(page, size, sort, direction, vendorId, from, to);
    }

    @GetMapping("/{id}")
    public Grn get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Grn update(@PathVariable Long id, @Valid @RequestBody GrnRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}