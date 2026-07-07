package com.posapi.infrastructure.adapter.output.persistence.adapter.supplier;

import com.posapi.domain.model.supplier.Supplier;
import com.posapi.domain.port.output.SupplierRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.supplier.SupplierPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.supplier.SupplierJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SupplierPersistenceAdapter implements SupplierRepository {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierPersistenceMapper supplierMapper;
    private final EntityManager entityManager;

    @Override
    public Supplier save(Supplier supplier) {
        SupplierEntity entity = supplierMapper.toEntity(supplier);
        SupplierEntity savedEntity = supplierJpaRepository.saveAndFlush(entity);
        entityManager.refresh(savedEntity);
        return supplierMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Supplier> findById(UUID id) {
        return supplierJpaRepository.findById(id).map(supplierMapper::toDomain);
    }

    @Override
    public List<Supplier> findAll() {
        return supplierJpaRepository.findAll().stream().map(supplierMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        supplierJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByRfc(String rfc) {
        return supplierJpaRepository.existsByRfc(rfc);
    }
}
