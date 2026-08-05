package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

import com.posapi.domain.model.purchase.PurchasePaymentStatus;
import com.posapi.domain.model.purchase.PurchaseStatus;
import com.posapi.infrastructure.adapter.input.rest.dto.BaseResponse;
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
public class PurchaseResponse extends BaseResponse {
    private UUID supplierId;
    private String supplierName;
    private Instant purchaseDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private PurchaseStatus status;
    private PurchasePaymentStatus paymentStatus;
    private String createdByName;
    private String updatedByName;
    private List<PurchaseItemResponse> items;
}
