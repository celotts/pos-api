package com.posapi.infrastructure.adapter.input.rest.accountspayable.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountsPayableRequest(
        @NotNull(message = "El ID de la compra es obligatorio")
        UUID purchaseId,

        @NotNull(message = "El ID del proveedor es obligatorio")
        UUID supplierId,

        @NotNull(message = "El monto original es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto original debe ser mayor que cero")
        BigDecimal originalAmount,

        @NotNull(message = "El monto pendiente es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El monto pendiente no puede ser negativo")
        BigDecimal outstandingAmount,

        LocalDate dueDate
) {}
