package com.project.fujicraft_management_system.Finance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceAuditRepository extends JpaRepository<FinanceAudit, Long> {
}