package com.posapi.infrastructure.adapter.output.persistence.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated; // Importar Generated
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType; // Importar EventType

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class ProductEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "category_id") // No nullable si hay FK
    private UUID categoryId;

    @Column(name = "purchase_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "current_stock", nullable = false, precision = 18, scale = 4)
    private BigDecimal currentStock;

    @Column(name = "tax_id")
    private UUID taxId;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Generated(event = EventType.INSERT) // Añadido
    @Column(name = "created_at", updatable = false, insertable = false) // Corregido insertable
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido
    @Column(name = "updated_at", insertable = false) // Corregido insertable
    private Instant updatedAt;

    @Column(name = "deleted_at") // AÑADIDO
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false) // Renombrado y añadido updatable
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id") // Renombrado
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id") // AÑADIDO
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT) // Añadido
    @Column(name = "created_by_role_id", updatable = false, insertable = false) // AÑADIDO
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido
    @Column(name = "updated_by_role_id", insertable = false) // AÑADIDO
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido
    @Column(name = "deleted_by_role_id", insertable = false) // AÑADIDO
    private UUID deletedByRoleId;
}
