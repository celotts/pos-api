package com.posapi.infrastructure.adapter.input.rest.accountspayable.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.accountspayable.AccountsPayable;
import com.posapi.domain.model.accountspayable.AccountsPayable.ArApStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AccountsPayableResponse(
    UUID id,
    UUID purchaseId,
    UUID supplierId,
    BigDecimal originalAmount,
    BigDecimal outstandingAmount,
    LocalDate dueDate,
    ArApStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt,
    UUID createdByUserId,
    UUID updatedByUserId,
    UUID deletedByUserId,
    UUID createdByUserRoleId,
    UUID updatedByUserRoleId,
    UUID deletedByUserRoleId,
    String createdByName,
    String updatedByName,
    String deletedByName
) {
    public static AccountsPayableResponse fromDomain(AccountsPayable accountsPayable, String createdByName, String updatedByName, String deletedByName) {
        return AccountsPayableResponse.builder()
                .id(accountsPayable.getId())
                .purchaseId(accountsPayable.getPurchaseId())
                .supplierId(accountsPayable.getSupplierId())
                .originalAmount(accountsPayable.getOriginalAmount())
                .outstandingAmount(accountsPayable.getOutstandingAmount())
                .dueDate(accountsPayable.getDueDate())
                .status(accountsPayable.getStatus())
                .createdAt(accountsPayable.getCreatedAt())
                .updatedAt(accountsPayable.getUpdatedAt())
                .deletedAt(accountsPayable.getDeletedAt())
                .createdByUserId(accountsPayable.getCreatedByUserId())
                .updatedByUserId(accountsPayable.getUpdatedByUserId())
                .deletedByUserId(accountsPayable.getDeletedByUserId())
                .createdByUserRoleId(accountsPayable.getCreatedByUserRoleId())
                .updatedByUserRoleId(accountsPayable.getUpdatedByUserRoleId())
                .deletedByUserRoleId(accountsPayable.getDeletedByUserRoleId())
                .createdByName(createdByName)
                .updatedByName(updatedByName)
                .deletedByName(deletedByName)
                .build();
    }

}
