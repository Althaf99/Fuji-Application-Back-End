package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MasterBatchRepository extends JpaRepository<MasterBatch, Long> {
    boolean existsByVendorId(Long vendorId);

    boolean existsByCodeIgnoreCase(String code);

    List<MasterBatch> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MasterBatch m where m.id = :id")
    Optional<MasterBatch> findByIdForUpdate(@Param("id") Long id);
}