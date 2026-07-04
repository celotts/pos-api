package com.posapi.infrastructure.adapter.input.rest.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductRequest(
    @NotBlank(message = "SKU cannot be blank")
    String sku,

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 255)
    String name,

    String description,

    @NotNull(message = "Sale price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be positive")
    BigDecimal salePrice,

    @NotNull(message = "Purchase price cannot be null")
    BigDecimal purchasePrice,

    @NotNull(message = "Stock cannot be null")
    BigDecimal currentStock,

    UUID taxId,
    UUID supplierId
) {
}
