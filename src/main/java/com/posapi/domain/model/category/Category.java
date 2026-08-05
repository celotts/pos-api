package com.posapi.domain.model.category;

import com.posapi.domain.model.base.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseModel {

    @Column(nullable = false, unique = true)
    private String name;

    public static Category createNew(String name, UUID currentUserId, UUID currentUserRoleId) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(name);
        category.setCreatedAt(Instant.now());
        category.setCreatedByUserId(currentUserId);
        category.setCreatedByUserRoleId(currentUserRoleId);
        return category;
    }

    public void updateName(String newName, UUID updatedByUserId, UUID updatedByRoleId) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or empty.");
        }
        this.name = newName;
        this.setUpdatedAt(Instant.now());
        this.setUpdatedByUserId(updatedByUserId);
        this.setUpdatedByUserRoleId(updatedByRoleId);
    }

    public void markAsDeleted(UUID deletedByUserId, UUID deletedByRoleId) {
        if (this.getDeletedAt() == null) {
            this.setDeletedAt(Instant.now());
            this.setDeletedByUserId(deletedByUserId);
            this.setDeletedByUserRoleId(deletedByRoleId);
        }
    }
}
