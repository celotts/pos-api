package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import com.posapi.domain.model.purchase.PurchasePaymentStatus;
import com.posapi.domain.model.purchase.PurchaseStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseRequest(
        @NotNull(message = "El ID del proveedor es obligatorio")
        UUID supplierId,

        @NotNull(message = "El monto total es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto total debe ser mayor que cero")
        BigDecimal totalAmount,

        @NotNull(message = "El monto total de impuestos es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El monto total de impuestos no puede ser negativo")
        BigDecimal totalTaxAmount,

        // Opcional para la creación, se establece por defecto en el dominio
        PurchaseStatus status,

        // Opcional para la creación, se establece por defecto en el dominio
        PurchasePaymentStatus paymentStatus
) {}
