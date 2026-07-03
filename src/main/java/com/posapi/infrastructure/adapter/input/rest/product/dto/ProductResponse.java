package com.posapi.infrastructure.adapter.input.rest.product.dto;

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
    UUID taxId,
    UUID supplierId,
    Instant createdAt,
    Instant updatedAt
) {}
