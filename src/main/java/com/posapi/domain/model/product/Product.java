package com.posapi.domain.model.product;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Product {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal currentStock;
    private UUID categoryId;
    private UUID taxId;
    private UUID supplierId;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
