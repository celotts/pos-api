package com.posapi.infrastructure.adapter.output.persistence.adapter.tax;

import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.port.output.TaxRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.tax.TaxEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.tax.TaxPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.tax.TaxJpaRepository;
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
public class TaxPersistenceAdapter implements TaxRepository {

    private final TaxJpaRepository taxJpaRepository;
    private final TaxPersistenceMapper taxPersistenceMapper;

    @Override
    public Tax save(Tax tax) {
        TaxEntity taxEntity = taxPersistenceMapper.toEntity(tax);
        return taxPersistenceMapper.toDomain(taxJpaRepository.save(taxEntity));
    }

    @Override
    public Optional<Tax> findById(UUID id) {
        return taxJpaRepository.findById(id)
                .map(taxPersistenceMapper::toDomain);
    }

    @Override
    public List<Tax> findAll() {
        return taxJpaRepository.findAll().stream()
                .map(taxPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Tax> findAll(Pageable pageable) {
        return taxJpaRepository.findAll(pageable)
                .map(taxPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        taxJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return taxJpaRepository.existsByName(name);
    }

    @Override
    public Optional<Tax> findByName(String name) {
        return taxJpaRepository.findByName(name)
                .map(taxPersistenceMapper::toDomain);
    }

    @Override
    public void deleteAll() {
        taxJpaRepository.deleteAll();
    }

    @Override
    public boolean existsById(UUID id) {
        return taxJpaRepository.existsById(id);
    }
}
