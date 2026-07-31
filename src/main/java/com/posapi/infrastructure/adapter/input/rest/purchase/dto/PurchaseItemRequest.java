package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

// Convertido a record para consistencia y para incluir el ID para actualizaciones
public record PurchaseItemRequest(
        UUID id, // ID del ítem de compra (null para nuevos ítems)
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
