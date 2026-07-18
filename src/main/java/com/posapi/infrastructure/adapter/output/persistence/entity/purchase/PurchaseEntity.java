package com.posapi.infrastructure.adapter.output.persistence.entity.purchase;

import com.posapi.infrastructure.adapter.output.persistence.entity.paymet.PaymentStatus;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal; // Importar BigDecimal
import java.time.Instant; // Cambiado de OffsetDateTime a Instant para consistencia
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // Añadido toBuilder
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private SupplierEntity supplier;

    @Column(name = "purchase_date", nullable = false)
    private Instant purchaseDate; // Cambiado de OffsetDateTime a Instant

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount; // CORREGIDO: Cambiado de double a BigDecimal

    @Column(name = "total_tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalTaxAmount; // CORREGIDO: Cambiado de double a BigDecimal

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PurchaseStatus status = PurchaseStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt; // Cambiado de OffsetDateTime a Instant

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt; // Cambiado de OffsetDateTime a Instant

    @Column(name = "deleted_at")
    private Instant deletedAt; // Cambiado de OffsetDateTime a Instant

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    private UserEntity createdByUser; // Renombrado para consistencia

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", referencedColumnName = "id")
    private UserEntity updatedByUser; // Renombrado para consistencia

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "id")
    private UserEntity deletedByUser; // Renombrado para consistencia

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
            this.createdAt = Instant.now();
        }
    }

    public enum PurchaseStatus {
        PENDING, COMPLETED, CANCELLED
    }
}
