package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class VendorController {
    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<Vendor> create(@Valid @RequestBody VendorRequest request) {
        return org.springframework.http.ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public PageResponse<Vendor> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return service.list(page, size, sort, direction);
    }

    @GetMapping("/{id}")
    public Vendor get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Vendor update(@PathVariable Long id, @Valid @RequestBody VendorRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}