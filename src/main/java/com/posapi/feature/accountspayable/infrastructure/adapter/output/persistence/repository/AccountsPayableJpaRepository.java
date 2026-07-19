package com.posapi.feature.accountspayable.infrastructure.adapter.output.persistence.repository;

import com.posapi.feature.accountspayable.infrastructure.adapter.output.persistence.entity.AccountsPayableEntity;
import com.posapi.domain.model.accountspayable.AccountsPayable; // Importar el enum del dominio
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountsPayableJpaRepository extends JpaRepository<AccountsPayableEntity, UUID> {
    Page<AccountsPayableEntity> findBySupplierId(UUID supplierId, Pageable pageable);
    Page<AccountsPayableEntity> findByStatus(AccountsPayable.ArApStatus status, Pageable pageable);
    Page<AccountsPayableEntity> findByDueDateBeforeAndStatus(LocalDate date, AccountsPayable.ArApStatus status, Pageable pageable);
    boolean existsById(UUID id);
}
