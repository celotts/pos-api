package com.posapi.infrastructure.adapter.input.rest.purchase.dto;

// Importación explícita y correcta para PurchaseItemRequest
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PurchaseRequest(
        @NotNull(message = "Supplier ID cannot be null")
        UUID supplierId,

        @NotNull(message = "Purchase date cannot be null")
        Instant purchaseDate,

        @Valid // Valida cada elemento de la lista
        @NotEmpty(message = "Purchase items cannot be empty")
        List<PurchaseItemRequest> items
) {
    // Los métodos de acceso (getters) para los componentes de un record se generan automáticamente.
    // No es necesario ni correcto definir getItems() o setItems() manualmente en un record.
}
