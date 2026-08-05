package com.posapi.infrastructure.adapter.input.rest.sale.dto;

import com.posapi.domain.model.sale.PaymentStatus;
import com.posapi.domain.model.sale.SaleStatus;
import com.posapi.infrastructure.adapter.input.rest.dto.BaseResponse;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse extends BaseResponse {
    private UUID customerId;
    private Instant saleDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal discountAmount;
    private SaleStatus status;
    private PaymentStatus paymentStatus;
    private UUID posTerminalId;
    private UUID shiftId;
    private List<SaleItemResponse> items;
    private String createdByName;
    private String updatedByName;
    private String deletedByName; // Added this line
    private String customerName;
}
