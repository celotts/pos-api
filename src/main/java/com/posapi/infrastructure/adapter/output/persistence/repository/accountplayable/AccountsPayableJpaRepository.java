package com.posapi.infrastructure.adapter.output.persistence.repository.accountplayable;

import com.posapi.domain.model.accountspayable.AccountsPayable;
import com.posapi.infrastructure.adapter.output.persistence.entity.accountspayable.AccountsPayableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;


@Repository
public interface AccountsPayableJpaRepository extends JpaRepository<AccountsPayableEntity, UUID> {
    Page<AccountsPayableEntity> findBySupplierId(UUID supplierId, Pageable pageable);
    Page<AccountsPayableEntity> findByStatus(AccountsPayable.ArApStatus status, Pageable pageable);
    Page<AccountsPayableEntity> findByDueDateBeforeAndStatus(LocalDate date, AccountsPayable.ArApStatus status, Pageable pageable);
    boolean existsById(UUID id);
}
