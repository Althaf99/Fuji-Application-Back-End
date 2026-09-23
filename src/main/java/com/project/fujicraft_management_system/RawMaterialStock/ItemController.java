package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")

public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping("/api/raw-materials")
    @ResponseStatus(HttpStatus.CREATED)
    public RawMaterial createRawMaterial(@Valid @RequestBody ItemRequest request) {
        return service.createRawMaterial(request);
    }

    @GetMapping("/api/raw-materials")
    public PageResponse<RawMaterial> listRawMaterials(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction, @RequestParam(required = false) String search) {
        return service.listRawMaterials(page, size, sort, direction, search);
    }

    @GetMapping("/api/raw-materials/{id}")
    public RawMaterial getRawMaterial(@PathVariable Long id) {
        return service.getRawMaterial(id);
    }

    @PutMapping("/api/raw-materials/{id}")
    public RawMaterial updateRawMaterial(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return service.updateRawMaterial(id, request);
    }

    @DeleteMapping("/api/raw-materials/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRawMaterial(@PathVariable Long id) {
        service.deleteRawMaterial(id);
    }

    @PostMapping("/api/master-batches")
    @ResponseStatus(HttpStatus.CREATED)
    public MasterBatch createMasterBatch(@Valid @RequestBody ItemRequest request) {
        return service.createMasterBatch(request);
    }

    @GetMapping("/api/master-batches")
    public PageResponse<MasterBatch> listMasterBatches(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction, @RequestParam(required = false) String search) {
        return service.listMasterBatches(page, size, sort, direction, search);
    }

    @GetMapping("/api/master-batches/{id}")
    public MasterBatch getMasterBatch(@PathVariable Long id) {
        return service.getMasterBatch(id);
    }

    @PutMapping("/api/master-batches/{id}")
    public MasterBatch updateMasterBatch(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return service.updateMasterBatch(id, request);
    }

    @DeleteMapping("/api/master-batches/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMasterBatch(@PathVariable Long id) {
        service.deleteMasterBatch(id);
    }
}