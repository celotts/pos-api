package com.posapi.infrastructure.adapter.input.rest.saleItem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class SaleItemRequest {
    // Getters y Setters
    private UUID productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice; // Puede ser útil si el precio unitario puede variar por venta

}
