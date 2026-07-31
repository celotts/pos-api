package com.posapi.infrastructure.adapter.input.rest.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemRequest {
    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotNull(message = "Unit price cannot be null")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
}
