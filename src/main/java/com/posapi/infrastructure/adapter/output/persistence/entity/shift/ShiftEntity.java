package com.posapi.infrastructure.adapter.output.persistence.entity.shift;

import com.posapi.infrastructure.adapter.output.persistence.entity.posterminal.PosTerminalEntity; // Mantener si se usa en otro lugar
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity; // Mantener si se usa en otro lugar
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shifts")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class ShiftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // CORREGIDO: Cambiado de UserEntity a UUID y eliminadas anotaciones @ManyToOne y @JoinColumn
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // CORREGIDO: Cambiado de PosTerminalEntity a UUID y eliminadas anotaciones @ManyToOne y @JoinColumn
    @Column(name = "pos_terminal_id", nullable = false)
    private UUID posTerminalId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "starting_cash", nullable = false, precision = 18, scale = 2)
    private BigDecimal startingCash;

    @Column(name = "ending_cash", precision = 18, scale = 2)
    private BigDecimal endingCash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShiftStatus status;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_by_role_id")
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_by_role_id")
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "deleted_by_role_id")
    private UUID deletedByRoleId;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.status == null) {
            this.status = ShiftStatus.OPEN;
        }
    }

    public enum ShiftStatus {
        OPEN, CLOSED, CANCELLED
    }
}
