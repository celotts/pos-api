package com.posapi.infrastructure.adapter.input.rest.cashaccount.dto;

import com.posapi.domain.model.cashaccount.CashAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashAccountRequest {
    @NotBlank
    private String name;

    @NotNull
    private CashAccountType accountType;

    @NotNull
    @PositiveOrZero
    private BigDecimal initialBalance;

    @NotBlank
    private String currency;
}
