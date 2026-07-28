package com.posapi.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {
    private UUID id;
    private UUID purchaseId;
    private UUID productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;

    // Método de dominio para crear un nuevo PurchaseItem
    public static PurchaseItem createNew(
            UUID purchaseId,
            UUID productId,
            BigDecimal quantity,
            BigDecimal unitPrice,
            UUID currentUserId, UUID currentUserRoleId) { // Eliminado currentUserRoleId
        BigDecimal subtotal = quantity.multiply(unitPrice);
        return PurchaseItem.builder()
                .id(UUID.randomUUID())
                .purchaseId(purchaseId)
                .productId(productId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .build();
    }

    // Método de dominio para actualizar un PurchaseItem (si fuera necesario)
    public void updateDetails(
            BigDecimal newQuantity,
            BigDecimal newUnitPrice,
            UUID updatedByUserId,
            UUID updatedByRoleId) { // Eliminado updatedByRoleId
        this.quantity = newQuantity;
        this.unitPrice = newUnitPrice;
        this.subtotal = newQuantity.multiply(newUnitPrice);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
    }

    // Método de dominio para marcado lógico de borrado
    public void markAsDeleted(UUID deletedByUserId) { // Eliminado deletedByRoleId
        this.deletedAt = Instant.now();
        this.deletedByUserId = deletedByUserId;
    }
}
