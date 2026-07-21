package com.posapi.infrastructure.adapter.output.persistence.repository.inventory;

import com.posapi.infrastructure.adapter.output.persistence.entity.inventory.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionJpaRepository extends JpaRepository<InventoryTransactionEntity, UUID> {
    List<InventoryTransactionEntity> findByProductId(UUID productId);
}
