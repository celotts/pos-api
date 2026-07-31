package com.posapi.infrastructure.adapter.input.rest.saleItem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemRequest(
        UUID id,
        @NotNull(message = "Product ID cannot be null")
        UUID productId,
        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        BigDecimal quantity,
        @NotNull(message = "Unit price cannot be null")
        @Positive(message = "Unit price must be positive")
        BigDecimal unitPrice
) {
}
