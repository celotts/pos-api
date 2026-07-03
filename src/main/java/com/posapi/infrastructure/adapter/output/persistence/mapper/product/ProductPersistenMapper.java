package com.posapi.infrastructure.adapter.output.persistence.mapper.product;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenMapper {

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
                .taxId(domain.getTaxId())
                .supplierId(domain.getSupplierId())
                // Omitimos createdAt y updatedAt para que Hibernate los gestione
                .deletedAt(domain.getDeletedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .deletedBy(domain.getDeletedBy())
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
                .taxId(entity.getTaxId())
                .supplierId(entity.getSupplierId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .deletedBy(entity.getDeletedBy())
                .build();
    }
}
