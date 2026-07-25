package com.posapi.infrastructure.adapter.output.persistence.mapper.inventory;

import com.posapi.domain.model.inventory.InventoryTransaction;
import com.posapi.infrastructure.adapter.output.persistence.entity.inventory.InventoryTransactionEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InventoryTransactionPersistenceMapper {

    public InventoryTransactionEntity toEntity(InventoryTransaction domain, ProductEntity productEntity) {
        if (domain == null) {
            return null;
        }
        return InventoryTransactionEntity.builder()
                .id(domain.getId())
                .product(productEntity)
                .transactionType(domain.getTransactionType())
                .quantityChange(domain.getQuantityChange())
                .newStock(domain.getNewStock())
                .sourceDocumentId(domain.getSourceDocumentId())
                .sourceDocumentType(domain.getSourceDocumentType())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .createdByUser(Optional.ofNullable(domain.getCreatedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .createdByRole(Optional.ofNullable(domain.getCreatedByRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .build();
    }

    public InventoryTransaction toDomain(InventoryTransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return InventoryTransaction.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .transactionType(entity.getTransactionType())
                .quantityChange(entity.getQuantityChange())
                .newStock(entity.getNewStock())
                .sourceDocumentId(entity.getSourceDocumentId())
                .sourceDocumentType(entity.getSourceDocumentType())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .createdByUserId(entity.getCreatedByUser() != null ? entity.getCreatedByUser().getId() : null)
                .createdByRoleId(entity.getCreatedByRole() != null ? entity.getCreatedByRole().getId() : null)
                .build();
    }

    public List<InventoryTransaction> toDomainList(List<InventoryTransactionEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<InventoryTransactionEntity> toEntityList(List<InventoryTransaction> domains, ProductEntity productEntity) {
        return domains.stream()
                .map(domain -> toEntity(domain, productEntity))
                .collect(Collectors.toList());
    }
}
