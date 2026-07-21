package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseItemRequest(
        @NotNull(message = "Product ID cannot be null")
        UUID productId,

        @NotNull(message = "Quantity cannot be null")
        @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
        BigDecimal quantity,

        @NotNull(message = "Unit price cannot be null")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
        BigDecimal unitPrice
) { }
