package com.posapi.infrastructure.adapter.input.rest.cashAccount.dto;

import com.posapi.domain.model.cashaccount.CashAccountType; // AÑADIDO: Importar CashAccountType
import jakarta.validation.constraints.DecimalMin; // AÑADIDO: Para validar initialBalance
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // AÑADIDO: Para validar initialBalance
import jakarta.validation.constraints.Size;

import java.math.BigDecimal; // AÑADIDO: Para initialBalance

public record CashAccountRequest(
        @NotBlank(message = "El nombre de la cuenta de caja no puede estar vacío")
        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 and 255 caracteres")
        String name,

        @NotNull(message = "El tipo de cuenta de caja es obligatorio") // CORREGIDO: @NotNull para enum
        CashAccountType accountType, // CORREGIDO: Cambiado a CashAccountType enum

        @NotNull(message = "El saldo inicial es obligatorio") // AÑADIDO: Saldo inicial
        @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial no puede ser negativo") // AÑADIDO: Saldo inicial no negativo
        BigDecimal initialBalance, // AÑADIDO: Campo para el saldo inicial

        @NotBlank(message = "La divisa es obligatoria")
        @Size(min = 3, max = 3, message = "La divisa debe ser un código ISO de 3 caracteres (ej: MXN)")
        String currency
) {}
