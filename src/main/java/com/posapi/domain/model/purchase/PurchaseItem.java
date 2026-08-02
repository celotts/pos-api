package com.posapi.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder para consistencia
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
    private UUID createdByUserRoleId; // Añadido
    private UUID updatedByUserRoleId; // Añadido
    private UUID deletedByUserRoleId; // Añadido

    // Método de dominio para crear un nuevo PurchaseItem
    public static PurchaseItem createNew(
            UUID purchaseId,
            UUID productId,
            BigDecimal quantity,
            BigDecimal unitPrice,
            UUID currentUserId,
            UUID currentUserRoleId) { // Incluido currentUserRoleId
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
                .createdByUserRoleId(currentUserRoleId) // Asignado
                .build();
    }

    // Método de dominio para actualizar un PurchaseItem
    public void updateDetails(
            BigDecimal newQuantity,
            BigDecimal newUnitPrice,
            UUID updatedByUserId,
            UUID updatedByUserRoleId) { // Incluido updatedByUserRoleId
        this.quantity = newQuantity;
        this.unitPrice = newUnitPrice;
        this.subtotal = newQuantity.multiply(newUnitPrice);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId; // Asignado
    }

    // Método de dominio para marcado lógico de borrado
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) { // Incluido deletedByUserRoleId
        this.deletedAt = Instant.now();
        this.deletedByUserId = deletedByUserId;
        this.deletedByUserRoleId = deletedByUserRoleId; // Asignado
    }
}
