package com.posapi.infrastructure.adapter.input.rest.product.dto;

import com.posapi.domain.model.product.Product; // Importar la entidad de dominio Product
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal salePrice,
        BigDecimal purchasePrice,
        BigDecimal currentStock,
        UUID categoryId, // Añadido
        UUID taxId,      // Añadido
        UUID supplierId, // Añadido
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt, // Añadido para auditoría
        UUID createdByUserId, // Añadido para auditoría
        UUID updatedByUserId, // Añadido para auditoría
        UUID deletedByUserId, // Añadido para auditoría
        UUID createdByUserRoleId, // Añadido para auditoría
        UUID updatedByUserRoleId, // Añadido para auditoría
        UUID deletedByUserRoleId, // Añadido para auditoría
        String createdByName, // Añadido para nombres de auditoría
        String updatedByName, // Añadido para nombres de auditoría
        String deletedByName  // Añadido para nombres de auditoría
) {
    // Método estático para mapear desde el dominio a la respuesta DTO
    public static ProductResponse fromDomain(Product product, String createdByName, String updatedByName, String deletedByName) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .salePrice(product.getSalePrice())
                .purchasePrice(product.getPurchasePrice())
                .currentStock(product.getCurrentStock())
                .categoryId(product.getCategoryId())
                .taxId(product.getTaxId())
                .supplierId(product.getSupplierId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .createdByUserId(product.getCreatedByUserId())
                .updatedByUserId(product.getUpdatedByUserId())
                .deletedByUserId(product.getDeletedByUserId())
                .createdByUserRoleId(product.getCreatedByUserRoleId())
                .updatedByUserRoleId(product.getUpdatedByUserRoleId())
                .deletedByUserRoleId(product.getDeletedByUserRoleId())
                .createdByName(createdByName)
                .updatedByName(updatedByName)
                .deletedByName(deletedByName)
                .build();
    }
}
