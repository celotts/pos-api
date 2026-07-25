package com.posapi.domain.port.output;

import com.posapi.domain.model.inventory.InventoryTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryTransactionRepository {
    InventoryTransaction save(InventoryTransaction inventoryTransaction);
    List<InventoryTransaction> saveAll(List<InventoryTransaction> inventoryTransactions);
    Optional<InventoryTransaction> findById(UUID id);
    List<InventoryTransaction> findByProductId(UUID productId);
}
