package com.posapi.infrastructure.adapter.input.rest.shift.dto;

import com.posapi.domain.model.shift.ShiftStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ShiftRequest(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID userId,

        @NotNull(message = "El ID de la terminal POS es obligatorio")
        UUID posTerminalId,

        @NotNull(message = "El efectivo inicial es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El efectivo inicial no puede ser negativo")
        BigDecimal startingCash,

        // Opcional para la creación, se establece por defecto en el dominio
        ShiftStatus status
) {}
