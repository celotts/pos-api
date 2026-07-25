package com.posapi.domain.model.purchase;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList; // AÑADIDO
import java.util.List; // AÑADIDO
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    private UUID id;
    private UUID supplierId;
    private Instant purchaseDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private PurchaseStatus status;
    private PurchasePaymentStatus paymentStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    private List<PurchaseItem> items; // AÑADIDO: Lista de ítems de la compra

    // Constructor para crear una nueva compra con valores por defecto
    public static Purchase createNew(
            UUID supplierId,
            Instant purchaseDate,
            List<PurchaseItem> items, // AÑADIDO: Recibe los ítems
            UUID currentUserId,
            UUID currentUserRoleId) {

        // Calcular totalAmount y totalTaxAmount a partir de los ítems
        BigDecimal calculatedTotalAmount = items.stream()
                .map(PurchaseItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Aquí deberías calcular el totalTaxAmount si los ítems tienen información de impuestos
        // Por ahora, lo dejamos en cero o lo calculamos de forma simplificada
        BigDecimal calculatedTotalTaxAmount = BigDecimal.ZERO; // Lógica de cálculo de impuestos más compleja

        return Purchase.builder()
                .id(UUID.randomUUID())
                .supplierId(supplierId)
                .purchaseDate(purchaseDate)
                .totalAmount(calculatedTotalAmount)
                .totalTaxAmount(calculatedTotalTaxAmount)
                .status(PurchaseStatus.PENDING)
                .paymentStatus(PurchasePaymentStatus.UNPAID)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .items(new ArrayList<>(items)) // Inicializar la lista de ítems
                .build();
    }

    // Método de dominio para marcar la compra como completada
    public void markAsCompleted(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status == PurchaseStatus.PENDING) {
            this.status = PurchaseStatus.COMPLETED;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
        } else {
            throw new IllegalStateException("Purchase cannot be completed from status: " + this.status);
        }
    }

    // Método de dominio para marcar la compra como cancelada
    public void markAsCancelled(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.status != PurchaseStatus.COMPLETED) {
            this.status = PurchaseStatus.CANCELLED;
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
        } else {
            throw new IllegalStateException("Completed purchase cannot be cancelled.");
        }
    }

    // Método de dominio para actualizar el estado de pago
    public void updatePaymentStatus(PurchasePaymentStatus newPaymentStatus,
                                    UUID updatedByUserId,
                                    UUID updatedByUserRoleId) {
        this.paymentStatus = newPaymentStatus;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método para recalcular totales si los ítems cambian
    public void recalculateTotals() {
        if (this.items != null && !this.items.isEmpty()) {
            this.totalAmount = this.items.stream()
                    .map(PurchaseItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // Lógica de cálculo de impuestos más compleja si los ítems tienen impuestos
            this.totalTaxAmount = BigDecimal.ZERO;
        } else {
            this.totalAmount = BigDecimal.ZERO;
            this.totalTaxAmount = BigDecimal.ZERO;
        }
    }
}
