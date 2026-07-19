package com.posapi.infrastructure.adapter.output.persistence.adapter.accounts;

import com.posapi.domain.model.accountspayable.AccountsPayable;
import com.posapi.domain.port.output.AccountsPayableRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.accountspayable.AccountsPayablePersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.accountplayable.AccountsPayableJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component // ¡IMPORTANTE! Para que Spring lo detecte como un bean
@RequiredArgsConstructor
public class AccountsPayablePersistenceAdapter implements AccountsPayableRepository {

    private final AccountsPayableJpaRepository accountsPayableJpaRepository;
    private final AccountsPayablePersistenceMapper accountsPayablePersistenceMapper;

    @Override
    public AccountsPayable save(AccountsPayable accountsPayable) {
        return accountsPayablePersistenceMapper.toDomain(
                accountsPayableJpaRepository.save(accountsPayablePersistenceMapper.toEntity(accountsPayable))
        );
    }

    @Override
    public Optional<AccountsPayable> findById(UUID id) {
        return accountsPayableJpaRepository.findById(id)
                .map(accountsPayablePersistenceMapper::toDomain);
    }

    @Override
    public Page<AccountsPayable> findAll(Pageable pageable) {
        return accountsPayableJpaRepository.findAll(pageable)
                .map(accountsPayablePersistenceMapper::toDomain);
    }

    @Override
    public List<AccountsPayable> findAll() {
        return accountsPayableJpaRepository.findAll().stream()
                .map(accountsPayablePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AccountsPayable> findBySupplierId(UUID supplierId, Pageable pageable) {
        return accountsPayableJpaRepository.findBySupplierId(supplierId, pageable)
                .map(accountsPayablePersistenceMapper::toDomain);
    }

    @Override
    public Page<AccountsPayable> findByStatus(AccountsPayable.ArApStatus status, Pageable pageable) {
        return accountsPayableJpaRepository.findByStatus(status, pageable)
                .map(accountsPayablePersistenceMapper::toDomain);
    }

    @Override
    public Page<AccountsPayable> findByDueDateBeforeAndStatus(LocalDate date, AccountsPayable.ArApStatus status, Pageable pageable) {
        return accountsPayableJpaRepository.findByDueDateBeforeAndStatus(date, status, pageable)
                .map(accountsPayablePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return accountsPayableJpaRepository.existsById(id);
    }
}

