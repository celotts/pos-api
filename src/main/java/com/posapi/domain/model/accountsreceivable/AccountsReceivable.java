package com.posapi.domain.model.accountsreceivable;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountsReceivable {
    private UUID id;
    private UUID saleId;
    private UUID customerId;
    private BigDecimal originalAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private ArApStatus status; // Usando el enum de dominio

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Método estático para crear una nueva cuenta por cobrar
    public static AccountsReceivable createNew(
            UUID saleId, UUID customerId, BigDecimal originalAmount, LocalDate dueDate,
            UUID currentUserId, UUID currentUserRoleId) {
        return AccountsReceivable.builder()
                .id(UUID.randomUUID())
                .saleId(saleId)
                .customerId(customerId)
                .originalAmount(originalAmount)
                .outstandingAmount(originalAmount) // Al inicio, outstanding es igual al original
                .dueDate(dueDate)
                .status(ArApStatus.OPEN) // Estado inicial por defecto
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para registrar un pago
    public void recordPayment(BigDecimal amount, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }
        if (this.status == ArApStatus.CLOSED) {
            throw new IllegalStateException("Cannot record payment on a closed account.");
        }

        this.outstandingAmount = this.outstandingAmount.subtract(amount);
        if (this.outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.outstandingAmount = BigDecimal.ZERO;
            this.status = ArApStatus.CLOSED; // Si el saldo es cero o negativo, la cuenta se cierra
        }
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para marcar como vencida
    public void markAsOverdue(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status == ArApStatus.OPEN && this.dueDate != null && this.dueDate.isBefore(LocalDate.now())) {
            this.status = ArApStatus.OVERDUE;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
        }
    }

    // Método de dominio para cerrar la cuenta (ej. por ajuste o condonación)
    public void markAsClosed(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status != ArApStatus.CLOSED) {
            this.status = ArApStatus.CLOSED;
            this.outstandingAmount = BigDecimal.ZERO; // Asegurarse de que el saldo sea cero al cerrar
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
        }
    }

    // Método de dominio para borrado lógico
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
            // Opcional: cambiar el estado a CANCELLED si aplica
        }
    }
}
