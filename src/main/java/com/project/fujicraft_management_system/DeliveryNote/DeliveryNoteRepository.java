package com.project.fujicraft_management_system.DeliveryNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DeliveryNoteRepository <T extends DeliveryNote> extends JpaRepository<T ,Integer>, JpaSpecificationExecutor<DeliveryNote> {
}
