package com.posapi.infrastructure.adapter.output.persistence.adapter.cashaccount;

import com.posapi.domain.model.cashaccount.CashAccount;
import com.posapi.domain.port.output.CashAccountRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.cashaccount.CashAccountPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.cashaccount.CashAccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CashAccountPersistenceAdapter implements CashAccountRepository {

    private final CashAccountJpaRepository cashAccountJpaRepository;
    private final CashAccountPersistenceMapper cashAccountPersistenceMapper;

    @Override
    public CashAccount save(CashAccount cashAccount) {
        return cashAccountPersistenceMapper.toDomain(
                cashAccountJpaRepository.save(cashAccountPersistenceMapper.toEntity(cashAccount))
        );
    }

    @Override
    public Optional<CashAccount> findById(UUID id) {
        return cashAccountJpaRepository.findById(id)
                .map(cashAccountPersistenceMapper::toDomain);
    }

    @Override
    public Page<CashAccount> findAll(Pageable pageable) {
        return cashAccountJpaRepository.findAll(pageable)
                .map(cashAccountPersistenceMapper::toDomain);
    }

    @Override
    public List<CashAccount> findAll() {
        return cashAccountJpaRepository.findAll().stream()
                .map(cashAccountPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CashAccount> findByName(String name) {
        return cashAccountJpaRepository.findByName(name)
                .map(cashAccountPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return cashAccountJpaRepository.existsByName(name);
    }
}
