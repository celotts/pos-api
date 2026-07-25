package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PurchaseRequest(
        @NotNull(message = "Supplier ID cannot be null")
        UUID supplierId,

        @NotNull(message = "Purchase date cannot be null")
        Instant purchaseDate,

        @Valid // Valida cada elemento de la lista
        @NotEmpty(message = "Purchase items cannot be empty")
        List<PurchaseItemRequest> items
) { }
