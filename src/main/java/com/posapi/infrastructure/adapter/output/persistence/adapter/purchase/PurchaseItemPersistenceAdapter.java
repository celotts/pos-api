package com.posapi.infrastructure.adapter.output.persistence.adapter.purchase;

import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.domain.port.output.PurchaseItemRepository;
import com.posapi.domain.port.output.ProductRepository; // Para resolver Product
import com.posapi.domain.port.output.PurchaseRepository; // Para resolver Purchase
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.purchase.PurchaseItemPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.purchase.PurchaseItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PurchaseItemPersistenceAdapter implements PurchaseItemRepository {

    private final PurchaseItemJpaRepository purchaseItemJpaRepository;
    private final PurchaseItemPersistenceMapper purchaseItemPersistenceMapper;
    private final ProductRepository productRepository; // Para obtener ProductEntity
    private final PurchaseRepository purchaseRepository; // Para obtener PurchaseEntity

    @Override
    public PurchaseItem save(PurchaseItem purchaseItem) {
        // Necesitamos cargar las entidades relacionadas para el mapper
        PurchaseEntity purchaseEntity = purchaseRepository.findById(purchaseItem.getPurchaseId())
                .map(purchase -> PurchaseEntity.builder().id(purchase.getId()).build()) // Solo necesitamos el ID para la relación
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found for ID: " + purchaseItem.getPurchaseId()));

        ProductEntity productEntity = productRepository.findById(purchaseItem.getProductId())
                .map(product -> ProductEntity.builder().id(product.getId()).build()) // Solo necesitamos el ID para la relación
                .orElseThrow(() -> new IllegalArgumentException("Product not found for ID: " + purchaseItem.getProductId()));

        PurchaseItemEntity purchaseItemEntity = purchaseItemPersistenceMapper.toEntity(purchaseItem, purchaseEntity, productEntity);
        return purchaseItemPersistenceMapper.toDomain(purchaseItemJpaRepository.save(purchaseItemEntity));
    }

    @Override
    public List<PurchaseItem> saveAll(List<PurchaseItem> purchaseItems) {
        if (purchaseItems == null || purchaseItems.isEmpty()) {
            return List.of();
        }

        // Asumimos que todos los purchaseItems pertenecen a la misma Purchase
        UUID purchaseId = purchaseItems.get(0).getPurchaseId();
        PurchaseEntity purchaseEntity = purchaseRepository.findById(purchaseId)
                .map(purchase -> PurchaseEntity.builder().id(purchase.getId()).build())
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found for ID: " + purchaseId));

        List<PurchaseItemEntity> purchaseItemEntities = purchaseItems.stream()
                .map(domain -> {
                    ProductEntity productEntity = productRepository.findById(domain.getProductId())
                            .map(product -> ProductEntity.builder().id(product.getId()).build())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found for ID: " + domain.getProductId()));
                    return purchaseItemPersistenceMapper.toEntity(domain, purchaseEntity, productEntity);
                })
                .collect(Collectors.toList());

        return purchaseItemPersistenceMapper.toDomainList(purchaseItemJpaRepository.saveAll(purchaseItemEntities));
    }

    @Override
    public Optional<PurchaseItem> findById(UUID id) {
        return purchaseItemJpaRepository.findById(id)
                .map(purchaseItemPersistenceMapper::toDomain);
    }

    @Override
    public List<PurchaseItem> findByPurchaseId(UUID purchaseId) {
        return purchaseItemPersistenceMapper.toDomainList(purchaseItemJpaRepository.findByPurchaseId(purchaseId));
    }

    @Override
    public void deleteById(UUID id) {
        purchaseItemJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<PurchaseItem> purchaseItems) {
        List<PurchaseItemEntity> entitiesToDelete = purchaseItems.stream()
                .map(domain -> PurchaseItemEntity.builder().id(domain.getId()).build()) // Solo necesitamos el ID para borrar
                .collect(Collectors.toList());
        purchaseItemJpaRepository.deleteAll(entitiesToDelete);
    }
}
