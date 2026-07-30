package com.posapi.infrastructure.adapter.input.rest.sale.dto;

import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class SaleRequest {
    // Getters y Setters
    private UUID customerId;
    private BigDecimal discountAmount;
    private UUID posTerminalId;
    private UUID shiftId;
    private List<SaleItemRequest> items;

}
