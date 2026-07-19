
package com.posapi.infrastructure.adapter.output.persistence.entity.accountspayable;
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
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "accounts_payable")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class AccountsPayableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // CORREGIDO: Cambiado de PurchaseEntity a UUID y eliminadas anotaciones @OneToOne y @JoinColumn
    @Column(name = "purchase_id", nullable = false, unique = true)
    private UUID purchaseId;

    // CORREGIDO: Cambiado de SupplierEntity a UUID y eliminadas anotaciones @ManyToOne y @JoinColumn
    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "original_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "outstanding_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArApStatus status;

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
            this.status = ArApStatus.OPEN;
        }
    }

    public enum ArApStatus {
        OPEN, CLOSED, OVERDUE, PENDING, PAID, CANCELED
    }
}
