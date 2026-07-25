package com.posapi.infrastructure.adapter.output.persistence.mapper.purchase;

import com.posapi.domain.model.purchase.Purchase;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PurchasePersistenceMapper {

    public PurchaseEntity toEntity(Purchase domain) {
        if (domain == null) {
            return null;
        }
        return PurchaseEntity.builder()
                .id(domain.getId())
                .supplier(Optional.ofNullable(domain.getSupplierId())
                        .map(id -> SupplierEntity.builder().id(id).build()).orElse(null))
                .purchaseDate(domain.getPurchaseDate())
                .totalAmount(domain.getTotalAmount())
                .totalTaxAmount(domain.getTotalTaxAmount())
                .status(domain.getStatus())
                .paymentStatus(domain.getPaymentStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUser(Optional.ofNullable(domain.getCreatedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .updatedByUser(Optional.ofNullable(domain.getUpdatedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .deletedByUser(Optional.ofNullable(domain.getDeletedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .createdByRole(Optional.ofNullable(domain.getCreatedByUserRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .updatedByRole(Optional.ofNullable(domain.getUpdatedByUserRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .deletedByRole(Optional.ofNullable(domain.getDeletedByUserRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .build();
    }

    public Purchase toDomain(PurchaseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Purchase.builder()
                .id(entity.getId())
                .supplierId(entity.getSupplier() != null ? entity.getSupplier().getId() : null)
                .purchaseDate(entity.getPurchaseDate())
                .totalAmount(entity.getTotalAmount())
                .totalTaxAmount(entity.getTotalTaxAmount())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUser() != null ? entity.getCreatedByUser().getId() : null)
                .updatedByUserId(entity.getUpdatedByUser() != null ? entity.getUpdatedByUser().getId() : null)
                .deletedByUserId(entity.getDeletedByUser() != null ? entity.getDeletedByUser().getId() : null)
                .createdByUserRoleId(entity.getCreatedByRole() != null ? entity.getCreatedByRole().getId() : null)
                .updatedByUserRoleId(entity.getUpdatedByRole() != null ? entity.getUpdatedByRole().getId() : null)
                .deletedByUserRoleId(entity.getDeletedByRole() != null ? entity.getDeletedByRole().getId() : null)
                // Note: PurchaseItems are not mapped here. They will be fetched separately by PurchaseItemRepository
                .build();
    }

    public List<Purchase> toDomainList(List<PurchaseEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
