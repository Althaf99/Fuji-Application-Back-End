package com.project.fujicraft_management_system.Finance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinanceCategoryRepository extends JpaRepository<FinanceCategoryOption, Long> {
    List<FinanceCategoryOption> findByActiveTrueOrderByLabelAsc();

    boolean existsByValueIgnoreCase(String value);
}