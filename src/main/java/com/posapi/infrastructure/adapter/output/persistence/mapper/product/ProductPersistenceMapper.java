package com.posapi.infrastructure.adapter.output.persistence.mapper.product;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        return ProductEntity.builder()
                .id(domain.getId())
                .sku(domain.getSku())
                .name(domain.getName())
                .description(domain.getDescription())
                .purchasePrice(domain.getPurchasePrice())
                .salePrice(domain.getSalePrice())
                .currentStock(domain.getCurrentStock())
                .categoryId(domain.getCategoryId())
                .taxId(domain.getTaxId())
                .supplierId(domain.getSupplierId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByUserRoleId())
                .updatedByRoleId(domain.getUpdatedByUserRoleId())
                .deletedByRoleId(domain.getDeletedByUserRoleId())
                .build();
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return Product.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .description(entity.getDescription())
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                .currentStock(entity.getCurrentStock())
                .categoryId(entity.getCategoryId())
                .taxId(entity.getTaxId())
                .supplierId(entity.getSupplierId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                .createdByUserRoleId(entity.getCreatedByRoleId())
                .updatedByUserRoleId(entity.getUpdatedByRoleId())
                .deletedByUserRoleId(entity.getDeletedByRoleId())
                .build();
    }
}
