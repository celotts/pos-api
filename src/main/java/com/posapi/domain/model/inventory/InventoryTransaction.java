package com.posapi.domain.model.inventory;

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
public class InventoryTransaction {
    private UUID id;
    private UUID productId;
    private TransactionType transactionType;
    private BigDecimal quantityChange; // Cantidad que se suma o resta
    private BigDecimal newStock;       // Stock después de la transacción
    private UUID sourceDocumentId;     // ID de la compra, venta, etc.
    private String sourceDocumentType; // Tipo de documento (e.g., 'PURCHASE', 'SALE')
    private String notes;
    private Instant createdAt;
    private UUID createdByUserId;
    private UUID createdByRoleId;

    // Método de dominio para crear una nueva transacción de inventario
    public static InventoryTransaction createNew(
            UUID productId,
            TransactionType transactionType,
            BigDecimal quantityChange,
            BigDecimal newStock,
            UUID sourceDocumentId,
            String sourceDocumentType,
            String notes,
            UUID currentUserId,
            UUID currentUserRoleId) {
        return InventoryTransaction.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .transactionType(transactionType)
                .quantityChange(quantityChange)
                .newStock(newStock)
                .sourceDocumentId(sourceDocumentId)
                .sourceDocumentType(sourceDocumentType)
                .notes(notes)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByRoleId(currentUserRoleId)
                .build();
    }
}
