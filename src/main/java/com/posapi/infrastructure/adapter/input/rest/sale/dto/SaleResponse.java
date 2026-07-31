package com.posapi.infrastructure.adapter.input.rest.sale.dto;


import com.posapi.domain.model.sale.SaleStatus;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import com.posapi.domain.model.sale.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {
    // Getters y Setters
    private UUID id;
    private UUID customerId;
    private Instant saleDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal discountAmount;
    private SaleStatus status;
    private PaymentStatus paymentStatus;
    private UUID posTerminalId;
    private UUID shiftId;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private List<SaleItemResponse> items;
    private String createdByName;
    private String updatedByName;
    private String customerName;

}
