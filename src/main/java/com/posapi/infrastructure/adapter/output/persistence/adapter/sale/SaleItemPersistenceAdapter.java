package com.posapi.infrastructure.adapter.output.persistence.adapter.sale;

import com.posapi.domain.model.sale.SaleItem;
import com.posapi.domain.port.output.SaleItemRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.sale.SaleItemPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.sale.SaleItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SaleItemPersistenceAdapter implements SaleItemRepository {

    private final SaleItemJpaRepository saleItemJpaRepository;
    private final SaleItemPersistenceMapper saleItemPersistenceMapper;

    @Override
    public SaleItem save(SaleItem saleItem) {
        SaleItemEntity saleItemEntity = saleItemPersistenceMapper.toEntity(saleItem);
        SaleItemEntity savedEntity = saleItemJpaRepository.save(saleItemEntity);
        return saleItemPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SaleItem> findById(UUID id) {
        return saleItemJpaRepository.findById(id)
                .map(saleItemPersistenceMapper::toDomain);
    }

    @Override
    public List<SaleItem> findAllBySaleId(UUID saleId) {
        List<SaleItemEntity> entities = saleItemJpaRepository.findAllBySaleId(saleId);
        return saleItemPersistenceMapper.toDomainList(entities);
    }

    @Override
    public Page<SaleItem> findAll(Pageable pageable) {
        return saleItemJpaRepository.findAll(pageable)
                .map(saleItemPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        saleItemJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return saleItemJpaRepository.existsById(id);
    }

    @Override
    public void delete(SaleItem entity) {
        saleItemJpaRepository.delete(saleItemPersistenceMapper.toEntity(entity));
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        saleItemJpaRepository.deleteAllById(ids);
    }
}
