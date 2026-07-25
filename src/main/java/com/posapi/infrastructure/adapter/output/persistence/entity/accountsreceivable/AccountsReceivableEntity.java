package com.posapi.infrastructure.adapter.output.persistence.entity.accountsreceivable;

import com.posapi.infrastructure.adapter.output.persistence.entity.customer.CustomerEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
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
@Table(name = "accounts_receivable")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class AccountsReceivableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", referencedColumnName = "id", nullable = false, unique = true)
    private SaleEntity sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private CustomerEntity customer;

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
        OPEN, CLOSED, OVERDUE
    }
}
