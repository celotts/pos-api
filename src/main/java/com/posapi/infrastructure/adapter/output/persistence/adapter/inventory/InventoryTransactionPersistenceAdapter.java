package com.posapi.infrastructure.adapter.output.persistence.adapter.inventory;

import com.posapi.domain.model.inventory.InventoryTransaction;
import com.posapi.domain.port.output.InventoryTransactionRepository;
import com.posapi.domain.port.output.ProductRepository; // Para resolver Product
import com.posapi.infrastructure.adapter.output.persistence.entity.inventory.InventoryTransactionEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.inventory.InventoryTransactionPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.inventory.InventoryTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryTransactionPersistenceAdapter implements InventoryTransactionRepository {

    private final InventoryTransactionJpaRepository inventoryTransactionJpaRepository;
    private final InventoryTransactionPersistenceMapper inventoryTransactionPersistenceMapper;
    private final ProductRepository productRepository; // Para obtener ProductEntity

    @Override
    public InventoryTransaction save(InventoryTransaction inventoryTransaction) {
        ProductEntity productEntity = productRepository.findById(inventoryTransaction.getProductId())
                .map(product -> ProductEntity.builder().id(product.getId()).build()) // Solo necesitamos el ID para la relación
                .orElseThrow(() -> new IllegalArgumentException("Product not found for ID: " + inventoryTransaction.getProductId()));

        InventoryTransactionEntity inventoryTransactionEntity = inventoryTransactionPersistenceMapper.toEntity(inventoryTransaction, productEntity);
        return inventoryTransactionPersistenceMapper.toDomain(inventoryTransactionJpaRepository.save(inventoryTransactionEntity));
    }

    @Override
    public List<InventoryTransaction> saveAll(List<InventoryTransaction> inventoryTransactions) {
        if (inventoryTransactions == null || inventoryTransactions.isEmpty()) {
            return List.of();
        }

        List<InventoryTransactionEntity> entities = inventoryTransactions.stream()
                .map(domain -> {
                    ProductEntity productEntity = productRepository.findById(domain.getProductId())
                            .map(product -> ProductEntity.builder().id(product.getId()).build())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found for ID: " + domain.getProductId()));
                    return inventoryTransactionPersistenceMapper.toEntity(domain, productEntity);
                })
                .collect(Collectors.toList());

        return inventoryTransactionPersistenceMapper.toDomainList(inventoryTransactionJpaRepository.saveAll(entities));
    }

    @Override
    public Optional<InventoryTransaction> findById(UUID id) {
        return inventoryTransactionJpaRepository.findById(id)
                .map(inventoryTransactionPersistenceMapper::toDomain);
    }

    @Override
    public List<InventoryTransaction> findByProductId(UUID productId) {
        return inventoryTransactionPersistenceMapper.toDomainList(inventoryTransactionJpaRepository.findByProductId(productId));
    }
}
