package com.posapi.infrastructure.adapter.input.rest.sale.dto;

// CORREGIDO: Importación correcta para SaleItemRequest
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class SaleRequest {
    private UUID customerId;
    private BigDecimal discountAmount;
    private UUID posTerminalId;
    private UUID shiftId;
    private List<SaleItemRequest> items;

    // Lombok generará los getters y setters.
    // Los comentarios de referencia se eliminan para mayor claridad.
}
