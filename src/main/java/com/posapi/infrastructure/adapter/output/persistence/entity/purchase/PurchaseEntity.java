package com.posapi.infrastructure.adapter.output.persistence.entity.purchase;

import com.posapi.domain.model.purchase.PurchasePaymentStatus; // CORREGIDO
import com.posapi.domain.model.purchase.PurchaseStatus; // CORREGIDO
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType; // AÑADIDO
import jakarta.persistence.Enumerated; // AÑADIDO
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Data // Usamos @Data en lugar de @Getter/@Setter para simplificar
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private SupplierEntity supplier;

    @Column(name = "purchase_date", nullable = false)
    private Instant purchaseDate;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalTaxAmount;

    @Enumerated(EnumType.STRING) // CORREGIDO
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PurchaseStatus status = PurchaseStatus.PENDING; // CORREGIDO: Usar enum de dominio

    @Enumerated(EnumType.STRING) // CORREGIDO
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PurchasePaymentStatus paymentStatus = PurchasePaymentStatus.UNPAID; // CORREGIDO: Usar enum de dominio

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    private UserEntity createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", referencedColumnName = "id")
    private UserEntity updatedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "id")
    private UserEntity deletedByUser;

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
}
