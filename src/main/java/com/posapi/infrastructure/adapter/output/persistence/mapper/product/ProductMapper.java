package com.posapi.infrastructure.adapter.output.persistence.mapper.product;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ProductMapper {

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;

        return ProductEntity.builder()
                .id(domain.getId())
                .sku(domain.getSku())
                .name(domain.getName())
                .description(domain.getDescription())
                .purchasePrice(domain.getPurchasePrice())
                .salePrice(domain.getSalePrice())
                // 🛡️ FIX: Pass BigDecimal directly. Avoid redundant and incorrect valueOf() call.
                .currentStock(domain.getCurrentStock())
                .taxId(domain.getTaxId())
                .supplierId(domain.getSupplierId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .build();
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        return Product.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .description(entity.getDescription())
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                // 🛡️ FIX: Pass BigDecimal directly. Avoid converting to int to prevent data loss.
                .currentStock(entity.getCurrentStock())
                .taxId(entity.getTaxId())
                .supplierId(entity.getSupplierId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .build();
    }
}