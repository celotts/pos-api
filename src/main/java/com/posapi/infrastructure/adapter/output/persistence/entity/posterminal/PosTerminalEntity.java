package com.posapi.infrastructure.adapter.output.persistence.entity.posterminal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pos_terminals")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class PosTerminalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String location;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // CORREGIDO: Campos de auditoría de usuario como UUID y eliminadas anotaciones @ManyToOne y @JoinColumn
    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    // CORREGIDO: Campos de auditoría de rol como UUID y eliminadas anotaciones @ManyToOne y @JoinColumn
    @Column(name = "created_by_role_id")
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id")
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id")
    private UUID deletedByRoleId;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}
