package com.posapi.infrastructure.adapter.input.rest.cashAccount.dto;

import com.fasterxml.jackson.annotation.JsonInclude;// Asumiendo tu ruta de dominio
import com.posapi.domain.model.cashaccount.CashAccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CashAccountResponse(
        UUID id,
        String name,
        String accountType,
        BigDecimal currentBalance,
        String currency,
        UUID createdByUserId,
        UUID updatedByUserId,
        UUID deletedByUserId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String createdByName,
        String updatedByName,
        String deletedByName
) {
    public static CashAccountResponse fromDomain(
            CashAccount cashAccount,
            String createdByName,
            String updatedByName,
            String deletedByName) {

        return new CashAccountResponse(
                cashAccount.getId(),
                cashAccount.getName(),
                cashAccount.getAccountType().name(), // Si en tu dominio es un Enum, usa .name() o .toString() aquí
                cashAccount.getCurrentBalance(),
                cashAccount.getCurrency(),
                cashAccount.getCreatedByUserId(),
                cashAccount.getUpdatedByUserId(),
                cashAccount.getDeletedByUserId(),
                cashAccount.getCreatedAt(),
                cashAccount.getUpdatedAt(),
                cashAccount.getDeletedAt(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
