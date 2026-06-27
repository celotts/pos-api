package com.posapi.infrastructure.adapter.input.rest.product.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal currentStock;
    private UUID taxId;
    private UUID supplierId;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    // Cambia estos tres de Long a UUID para alinearlos con tu dominio:
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
}