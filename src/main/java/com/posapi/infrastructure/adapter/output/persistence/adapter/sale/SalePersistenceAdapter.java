package com.posapi.infrastructure.adapter.output.persistence.adapter.sale;

import com.posapi.domain.model.sale.Sale;
import com.posapi.domain.port.output.SaleRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.sale.SalePersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.sale.SaleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SalePersistenceAdapter implements SaleRepository {

    private final SaleJpaRepository saleJpaRepository;
    private final SalePersistenceMapper salePersistenceMapper;

    @Override
    public Sale save(Sale sale) {
        SaleEntity saleEntity = salePersistenceMapper.toEntity(sale);
        SaleEntity savedEntity = saleJpaRepository.save(saleEntity);
        return salePersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return saleJpaRepository.findById(id)
                .map(salePersistenceMapper::toDomain);
    }

    @Override
    public List<Sale> findAll() {
        List<SaleEntity> entities = saleJpaRepository.findAll();
        return salePersistenceMapper.toDomainList(entities);
    }

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        Page<SaleEntity> entitiesPage = saleJpaRepository.findAll(pageable);
        return entitiesPage.map(salePersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        saleJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return saleJpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return saleJpaRepository.count();
    }

    @Override
    public void delete(Sale entity) {
        saleJpaRepository.delete(salePersistenceMapper.toEntity(entity));
    }

    @Override
    public void deleteAll() {
        saleJpaRepository.deleteAll();
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        saleJpaRepository.deleteAllById(ids);
    }
}
