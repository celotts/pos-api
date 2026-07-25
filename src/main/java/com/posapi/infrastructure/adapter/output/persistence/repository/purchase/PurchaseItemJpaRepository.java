package com.posapi.infrastructure.adapter.output.persistence.repository.purchase;

import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseItemJpaRepository extends JpaRepository<PurchaseItemEntity, UUID> {
    List<PurchaseItemEntity> findByPurchaseId(UUID purchaseId);
}
