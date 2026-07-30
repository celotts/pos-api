package com.posapi.infrastructure.adapter.input.rest.product.dto;

import com.posapi.domain.model.product.ProductType; // Importar ProductType
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
    @NotBlank String sku,
    @NotBlank String name,
    String description,
    @NotNull @PositiveOrZero BigDecimal purchasePrice,
    @NotNull @PositiveOrZero BigDecimal salePrice,
    @NotNull @PositiveOrZero BigDecimal currentStock,
    @NotNull UUID categoryId,
    UUID taxId,
    UUID supplierId,
    @NotNull ProductType productType // NUEVO: Atributo para el tipo de producto
) { }
