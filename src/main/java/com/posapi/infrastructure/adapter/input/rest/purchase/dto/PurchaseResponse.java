package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.purchase.Purchase;
import com.posapi.domain.model.purchase.PurchasePaymentStatus;
import com.posapi.domain.model.purchase.PurchaseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

// Importación necesaria para PurchaseItemResponse
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PurchaseResponse(
        UUID id,
        UUID supplierId,
        Instant purchaseDate,
        BigDecimal totalAmount,
        BigDecimal totalTaxAmount,
        PurchaseStatus status,
        PurchasePaymentStatus paymentStatus,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        UUID createdByUserId,
        UUID updatedByUserId,
        UUID deletedByUserId,
        UUID createdByUserRoleId,
        UUID updatedByUserRoleId,
        UUID deletedByUserRoleId,
        String createdByName,
        String updatedByName,
        String deletedByName,
        List<PurchaseItemResponse> items // Lista de ítems de la compra
) {
    public static PurchaseResponse fromDomain(
            Purchase purchase,
            List<PurchaseItemResponse> items, // Recibe los ítems
            String createdByName,
            String updatedByName,
            String deletedByName) {
        return new PurchaseResponse(
                purchase.getId(),
                purchase.getSupplierId(),
                purchase.getPurchaseDate(),
                purchase.getTotalAmount(),
                purchase.getTotalTaxAmount(),
                purchase.getStatus(),
                purchase.getPaymentStatus(),
                purchase.getCreatedAt(),
                purchase.getUpdatedAt(),
                purchase.getDeletedAt(),
                purchase.getCreatedByUserId(),
                purchase.getUpdatedByUserId(),
                purchase.getDeletedByUserId(),
                purchase.getCreatedByUserRoleId(),
                purchase.getUpdatedByUserRoleId(),
                purchase.getDeletedByUserRoleId(),
                createdByName,
                updatedByName,
                deletedByName,
                items
        );
    }
}
