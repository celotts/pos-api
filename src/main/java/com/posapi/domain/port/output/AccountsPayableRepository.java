package com.posapi.domain.port.output;

import com.posapi.domain.model.accountspayable.AccountsPayable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountsPayableRepository {
    AccountsPayable save(AccountsPayable accountsPayable);
    Optional<AccountsPayable> findById(UUID id);
    Page<AccountsPayable> findAll(Pageable pageable);
    List<AccountsPayable> findAll();
    Page<AccountsPayable> findBySupplierId(UUID supplierId, Pageable pageable);
    Page<AccountsPayable> findByStatus(AccountsPayable.ArApStatus status, Pageable pageable);
    Page<AccountsPayable> findByDueDateBeforeAndStatus(LocalDate date,
                                                        AccountsPayable.ArApStatus status,
                                                        Pageable pageable);
    boolean existsById(UUID id);
}
