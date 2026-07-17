package com.posapi.infrastructure.adapter.output.persistence.entity.purchase;


/*
CREATE TABLE IF NOT EXISTS purchases (
    id UUID PRIMARY KEY,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    purchase_date TIMESTAMPTZ DEFAULT NOW(),
    total_amount DECIMAL(18,2) NOT NULL,
    total_tax_amount DECIMAL(18,2) NOT NULL,
    status purchase_status NOT NULL DEFAULT 'PENDING',
    payment_status payment_status NOT NULL DEFAULT 'UNPAID',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by_user_id UUID REFERENCES users(id),
    updated_by_user_id UUID REFERENCES users(id),
    deleted_by_user_id UUID REFERENCES users(id),
    created_by_role_id UUID REFERENCES roles(id),
    updated_by_role_id UUID REFERENCES roles(id),
    deleted_by_role_id UUID REFERENCES roles(id)
);
 */

import com.posapi.infrastructure.adapter.output.persistence.entity.paymet.PaymentStatus;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private SupplierEntity supplier;

    @Column(name = "purchase_date", nullable = false)
    private OffsetDateTime purchaseDate;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private double totalAmount;

    @Column(name = "total_tax_amount", nullable = false, precision = 18, scale = 2)
    private double totalTaxAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PurchaseStatus status = PurchaseStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    private UserEntity createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id", referencedColumnName = "id")
    private UserEntity updatedBy;


    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "id")
    private UserEntity deletedBy;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }

}
