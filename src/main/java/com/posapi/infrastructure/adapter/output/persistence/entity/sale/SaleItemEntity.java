package com.posapi.infrastructure.adapter.output.persistence.entity.sale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sale_items")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class SaleItemEntity {

    @Id
    private UUID id;

    @Column(name = "sale_id", nullable = false)
    private UUID saleId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false)
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_by_role_id", updatable = false, insertable = false)
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_by_role_id", insertable = false)
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "deleted_by_role_id", insertable = false)
    private UUID deletedByRoleId;
}
