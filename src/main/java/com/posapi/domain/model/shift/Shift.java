package com.posapi.domain.model.shift;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    private UUID id;
    private UUID userId;
    private UUID posTerminalId;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal startingCash;
    private BigDecimal endingCash;
    private ShiftStatus status;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Método estático para crear un nuevo turno
    public static Shift createNew(
            UUID userId, UUID posTerminalId, BigDecimal startingCash,
            UUID currentUserId, UUID currentUserRoleId) {
        if (startingCash.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Starting cash cannot be negative.");
        }
        return Shift.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .posTerminalId(posTerminalId)
                .startTime(Instant.now())
                .startingCash(startingCash)
                .status(ShiftStatus.OPEN)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para cerrar un turno
    public void closeShift(BigDecimal endingCash, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status != ShiftStatus.OPEN) {
            throw new IllegalStateException("Cannot close a shift that is not OPEN.");
        }
        if (endingCash.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Ending cash cannot be negative.");
        }
        this.endTime = Instant.now();
        this.endingCash = endingCash;
        this.status = ShiftStatus.CLOSED;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para cancelar un turno
    public void cancelShift(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status == ShiftStatus.CLOSED) {
            throw new IllegalStateException("Cannot cancel a shift that is already CLOSED.");
        }
        this.status = ShiftStatus.CANCELLED;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
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
