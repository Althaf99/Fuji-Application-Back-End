package com.project.fujicraft_management_system.RawMaterialStock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    boolean existsByCodeIgnoreCase(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RawMaterial r where r.id = :id")
    Optional<RawMaterial> findByIdForUpdate(@Param("id") Long id);
}