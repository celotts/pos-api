package com.posapi.domain.port.output;

import com.posapi.domain.model.purchase.PurchaseItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseItemRepository {
    PurchaseItem save(PurchaseItem purchaseItem);
    List<PurchaseItem> saveAll(List<PurchaseItem> purchaseItems); // Para guardar múltiples ítems de una compra
    Optional<PurchaseItem> findById(UUID id);
    List<PurchaseItem> findByPurchaseId(UUID purchaseId);
    void deleteById(UUID id);
    void deleteAll(List<PurchaseItem> purchaseItems);
}
