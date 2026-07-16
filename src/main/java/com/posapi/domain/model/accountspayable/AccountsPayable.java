package com.posapi.domain.model.accountspayable;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AccountsPayable {
    private UUID id;
    private UUID purchaseId;
    private UUID supplierId;
    private BigDecimal originalAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private String status;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private UUID createdByUserId;
    private UUID updatedByUserId;

    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

}
