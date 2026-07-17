package com.posapi.infrastructure.adapter.output.persistence.entity.accountspayable;

import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts_payable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountsPayableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Relación uno a uno ya que purchase_id es UNIQUE en el DDL
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", referencedColumnName = "id", nullable = false, unique = true)
    private PurchaseEntity purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private SupplierEntity supplier;

    @Column(name = "original_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "outstanding_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ArApStatus status = ArApStatus.OPEN;

    // =============================================================================
    // METADATOS Y MARCAS DE TIEMPO
    // =============================================================================

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    // =============================================================================
    // AUDITORÍA POR USUARIOS
    // =============================================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    private UserEntity createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", referencedColumnName = "id")
    private UserEntity updatedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "id")
    private UserEntity deletedByUser;

    // =============================================================================
    // AUDITORÍA POR ROLES (Asignados automáticamente por tu Trigger SQL)
    // =============================================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_role_id", referencedColumnName = "id")
    private RoleEntity createdByRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_role_id", referencedColumnName = "id")
    private RoleEntity updatedByRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_role_id", referencedColumnName = "id")
    private RoleEntity deletedByRole;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }
}
