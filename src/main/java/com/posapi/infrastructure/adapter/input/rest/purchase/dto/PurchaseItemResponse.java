package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.purchase.PurchaseItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PurchaseItemResponse(
        UUID id,
        UUID productId,
        String productName,
        String productSku,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        UUID createdByUserId,
        UUID updatedByUserId,
        UUID deletedByUserId,
        // Eliminados los campos de RoleId ya que no existen en PurchaseItem de dominio
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
                purchaseItem.getProductId(),
                productName,
                productSku,
                purchaseItem.getQuantity(),
                purchaseItem.getUnitPrice(),
                purchaseItem.getSubtotal(), // CORREGIDO: Usar getSubtotal()
                purchaseItem.getCreatedAt(),
                purchaseItem.getUpdatedAt(),
                purchaseItem.getDeletedAt(),
                purchaseItem.getCreatedByUserId(),
                purchaseItem.getUpdatedByUserId(),
                purchaseItem.getDeletedByUserId(),
                // Eliminados los campos de RoleId
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
