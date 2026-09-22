package com.project.fujicraft_management_system.RawMaterialStock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GrnRepository extends JpaRepository<Grn, Long> {
    boolean existsByVendorId(Long vendorId);
}