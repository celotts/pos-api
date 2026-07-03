package com.posapi.infrastructure.adapter.output.persistence.entity.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@SQLRestriction("deleted_at IS NULL")
public class RoleEntity {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    @ToString.Include
    private String name;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id")
    private UUID createdBy;

    @Column(name = "updated_by_user_id")
    private UUID updatedBy;

    @Column(name = "deleted_by_user_id")
    private UUID deletedBy;

    @Column(name = "created_by_role_id", insertable = false, updatable = false)
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id", insertable = false, updatable = false)
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id", insertable = false, updatable = false)
    private UUID deletedByRoleId;
}
