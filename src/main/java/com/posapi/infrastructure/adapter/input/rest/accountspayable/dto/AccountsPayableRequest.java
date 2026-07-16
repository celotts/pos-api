package com.posapi.infrastructure.adapter.input.rest.accountspayable.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AccountsPayableRequest {
    private UUID purchaseId;
    private UUID supplierId;
    private BigDecimal originalAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private String status;
}
