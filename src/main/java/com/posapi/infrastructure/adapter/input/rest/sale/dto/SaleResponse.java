package com.posapi.infrastructure.adapter.input.rest.sale.dto;


import com.posapi.domain.model.sale.SaleStatus;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import com.posapi.infrastructure.adapter.output.persistence.entity.paymet.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class SaleResponse {
    // Getters y Setters
    private UUID id;
    private UUID customerId;
    private OffsetDateTime saleDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal discountAmount;
    private SaleStatus status;
    private PaymentStatus paymentStatus;
    private UUID posTerminalId;
    private UUID shiftId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private List<SaleItemResponse> items;

}
