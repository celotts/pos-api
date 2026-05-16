package com.posapi.infrastructure.adapter.input.rest.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "SKU cannot be empty")
    private String sku;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Purchase price cannot be null")
    @PositiveOrZero(message = "Purchase price must be positive or zero")
    private BigDecimal purchasePrice;

    @NotNull(message = "Sale price cannot be null")
    @PositiveOrZero(message = "Sale price must be positive or zero")
    private BigDecimal salePrice;

    @NotNull(message = "Current stock cannot be null")
    @PositiveOrZero(message = "Current stock must be positive or zero")
    private BigDecimal currentStock;

    private UUID taxId;
    private UUID supplierId;
    // createdByUserId, updatedByUserId, etc. serán manejados por la lógica de negocio/seguridad
}
