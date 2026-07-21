package com.posapi.infrastructure.adapter.output.persistence.adapter.purchase;

import com.posapi.domain.model.purchase.Purchase;
import com.posapi.domain.port.output.PurchaseRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.purchase.PurchasePersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.purchase.PurchaseJpaRepository;
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
public class PurchasePersistenceAdapter implements PurchaseRepository {

    private final PurchaseJpaRepository purchaseJpaRepository;
    private final PurchasePersistenceMapper purchasePersistenceMapper;

    @Override
    public Purchase save(Purchase purchase) {
        PurchaseEntity purchaseEntity = purchasePersistenceMapper.toEntity(purchase);
        return purchasePersistenceMapper.toDomain(purchaseJpaRepository.save(purchaseEntity));
    }

    @Override
    public Optional<Purchase> findById(UUID id) {
        return purchaseJpaRepository.findById(id)
                .map(purchasePersistenceMapper::toDomain);
    }

    @Override
    public List<Purchase> findAll() {
        return purchaseJpaRepository.findAll().stream()
                .map(purchasePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Purchase> findAll(Pageable pageable) {
        return purchaseJpaRepository.findAll(pageable)
                .map(purchasePersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        purchaseJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return purchaseJpaRepository.existsById(id);
    }
}
