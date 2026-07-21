package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.purchase.PurchaseItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PurchaseItemResponse(
        UUID id,
        UUID purchaseId,
        UUID productId,
        String productName, // Nombre del producto para mostrar en la respuesta
        String productSku,  // SKU del producto para mostrar en la respuesta
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String createdByName,
        String updatedByName,
        String deletedByName
) {
    public static PurchaseItemResponse fromDomain(
            PurchaseItem purchaseItem,
            String productName,
            String productSku,
            String createdByName,
            String updatedByName,
            String deletedByName) {
        return new PurchaseItemResponse(
                purchaseItem.getId(),
                purchaseItem.getPurchaseId(),
                purchaseItem.getProductId(),
                productName,
                productSku,
                purchaseItem.getQuantity(),
                purchaseItem.getUnitPrice(),
                purchaseItem.getSubtotal(),
                purchaseItem.getCreatedAt(),
                purchaseItem.getUpdatedAt(),
                purchaseItem.getDeletedAt(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
