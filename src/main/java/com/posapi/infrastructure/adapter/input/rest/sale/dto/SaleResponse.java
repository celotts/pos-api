package com.posapi.infrastructure.adapter.input.rest.sale.dto;

import com.posapi.domain.model.sale.PaymentStatus;
import com.posapi.domain.model.sale.SaleStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        UUID customerId,
        Instant saleDate,
        BigDecimal totalAmount,
        BigDecimal totalTaxAmount,
        BigDecimal discountAmount,
        SaleStatus status,
        PaymentStatus paymentStatus,
        UUID posTerminalId,
        UUID shiftId,
        Instant createdAt,
        Instant updatedAt,
        UUID createdByUserId,
        UUID updatedByUserId,
        List<SaleItemResponse> items,
        String createdByName,
        String updatedByName,
        String deletedByName
) {}
