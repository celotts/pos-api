package com.posapi.domain.model.accountspayable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor // Añadido
@AllArgsConstructor // Añadido
public class AccountsPayable {
    private UUID id;
    private UUID purchaseId;
    private UUID supplierId;
    private BigDecimal originalAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private ArApStatus status; // CORREGIDO: Ahora es del tipo enum ArApStatus

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Enum para el estado de las cuentas por pagar/cobrar
    public enum ArApStatus {
        OPEN, CLOSED, OVERDUE, PENDING, PAID, CANCELED // Asegúrate de que estos valores coincidan con tu DDL
    }

    // Método estático para crear una nueva cuenta por pagar
    public static AccountsPayable createNew(
            UUID purchaseId, UUID supplierId, BigDecimal originalAmount, LocalDate dueDate,
            UUID currentUserId, UUID currentUserRoleId) {
        return AccountsPayable.builder()
                .id(UUID.randomUUID())
                .purchaseId(purchaseId)
                .supplierId(supplierId)
                .originalAmount(originalAmount)
                .outstandingAmount(originalAmount) // Al inicio, outstanding es igual al original
                .dueDate(dueDate)
                .status(ArApStatus.OPEN) // Estado inicial
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para registrar un pago
    public void recordPayment(BigDecimal amountPaid, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount paid must be greater than zero.");
        }
        if (this.outstandingAmount.compareTo(amountPaid) < 0) {
            throw new IllegalArgumentException("Amount paid exceeds outstanding amount.");
        }

        this.outstandingAmount = this.outstandingAmount.subtract(amountPaid);
        if (this.outstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = ArApStatus.PAID;
        } else {
            // Podrías tener un estado PARTIALLY_PAID si lo necesitas
            this.status = ArApStatus.OPEN; // O un estado específico para pago parcial
        }
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para marcar como cancelado
    public void markAsCanceled(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status != ArApStatus.PAID) { // No se puede cancelar si ya está pagado
            this.status = ArApStatus.CANCELED;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
        } else {
            throw new IllegalStateException("Cannot cancel a paid accounts payable.");
        }
    }

    // Método de dominio para borrado lógico
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
        }
    }
}
