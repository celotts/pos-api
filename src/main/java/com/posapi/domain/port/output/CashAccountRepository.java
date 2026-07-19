package com.posapi.domain.port.output;

import com.posapi.domain.model.cashaccount.CashAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashAccountRepository {
    CashAccount save(CashAccount cashAccount);
    Optional<CashAccount> findById(UUID id);
    Page<CashAccount> findAll(Pageable pageable);
    List<CashAccount> findAll();
    Optional<CashAccount> findByName(String name);
    boolean existsByName(String name);
}
