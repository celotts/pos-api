package com.posapi.infrastructure.adapter.output.persistence.entity.purchase;

import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_items")
public class PurchaseItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private PurchaseEntity purchase; // Relación con PurchaseEntity

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product; // Relación con ProductEntity

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private UserEntity createdByUser;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private UserEntity updatedByUser;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id")
    private UserEntity deletedByUser;

    @ManyToOne
    @JoinColumn(name = "created_by_role_id")
    private RoleEntity createdByRole;

    @ManyToOne
    @JoinColumn(name = "updated_by_role_id")
    private RoleEntity updatedByRole;

    @ManyToOne
    @JoinColumn(name = "deleted_by_role_id")
    private RoleEntity deletedByRole;
}
