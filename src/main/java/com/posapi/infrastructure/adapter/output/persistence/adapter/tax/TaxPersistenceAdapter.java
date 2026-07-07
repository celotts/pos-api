package com.posapi.infrastructure.adapter.output.persistence.adapter.tax;

import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.port.output.TaxRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.tax.TaxEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.tax.TaxPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.tax.TaxJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaxPersistenceAdapter implements TaxRepository {

    private final TaxJpaRepository taxJpaRepository;
    private final TaxPersistenceMapper taxMapper;
    private final EntityManager entityManager;

    @Override
    public Tax save(Tax tax) {
        TaxEntity entity = taxMapper.toEntity(tax);
        TaxEntity savedEntity = taxJpaRepository.saveAndFlush(entity);
        entityManager.refresh(savedEntity);
        return taxMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Tax> findById(UUID id) {
        return taxJpaRepository.findById(id).map(taxMapper::toDomain);
    }

    @Override
    public List<Tax> findAll() {
        return taxJpaRepository.findAll().stream().map(taxMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        taxJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return taxJpaRepository.existsByName(name);
    }
}
