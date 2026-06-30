package com.posapi.infrastructure.adapter.input.rest.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.product.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    // --- Standard Fields ---
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    // --- Product-Specific Fields ---
    private String sku;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal currentStock;
    private UUID taxId;
    private UUID supplierId;
    private UUID createdBy;
    private UUID updatedBy;
    private boolean active;
    private boolean deleted;


    public static ProductResponse fromDomain(Product product) {
        boolean isDeleted = product.getDeletedAt() != null;
        return ProductResponse.builder()
                .id(product.getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .currentStock(product.getCurrentStock())
                .taxId(product.getTaxId())
                .supplierId(product.getSupplierId())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .active(!isDeleted)
                .deleted(isDeleted)
                .build();
    }
}
