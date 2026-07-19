package com.posapi.domain.model.purchase;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor; // Añadido para el constructor completo de Lombok
import lombok.NoArgsConstructor; // Añadido para el constructor sin argumentos de Lombok

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder para facilitar actualizaciones
@NoArgsConstructor // Añadido para JPA y otros frameworks
@AllArgsConstructor // Añadido para el builder
public class Purchase {
    private UUID id;
    private UUID supplierId;
    private Instant purchaseDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private PurchaseStatus status; // Usando el enum de dominio
    private PurchasePaymentStatus paymentStatus; // Usando el enum de dominio
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Constructor para crear una nueva compra con valores por defecto
    // Esto hace la clase menos anémica y encapsula la lógica de creación
    public static Purchase createNew(
            UUID supplierId, Instant purchaseDate, BigDecimal totalAmount, BigDecimal totalTaxAmount,
            UUID currentUserId, UUID currentUserRoleId) {
        return Purchase.builder()
                .id(UUID.randomUUID())
                .supplierId(supplierId)
                .purchaseDate(purchaseDate)
                .totalAmount(totalAmount)
                .totalTaxAmount(totalTaxAmount)
                .status(PurchaseStatus.PENDING) // Estado inicial por defecto
                .paymentStatus(PurchasePaymentStatus.UNPAID) // Estado de pago inicial por defecto
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para marcar la compra como completada
    public void markAsCompleted(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status == PurchaseStatus.PENDING) {
            this.status = PurchaseStatus.COMPLETED;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
            // Podrías añadir más lógica aquí, como actualizar el inventario
        } else {
            throw new IllegalStateException("Purchase cannot be completed from status: " + this.status);
        }
    }

    // Método de dominio para marcar la compra como cancelada
    public void markAsCancelled(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.status != PurchaseStatus.COMPLETED) { // No se puede cancelar una compra ya completada
            this.status = PurchaseStatus.CANCELLED;
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
        } else {
            throw new IllegalStateException("Completed purchase cannot be cancelled.");
        }
    }

    // Método de dominio para actualizar el estado de pago
    public void updatePaymentStatus(PurchasePaymentStatus newPaymentStatus, UUID updatedByUserId, UUID updatedByUserRoleId) {
        this.paymentStatus = newPaymentStatus;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }
}
