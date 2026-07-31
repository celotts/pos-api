package com.posapi.infrastructure.adapter.output.persistence.mapper.sale;

import com.posapi.domain.model.sale.SaleItem;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaleItemPersistenceMapper {

    public SaleItemEntity toEntity(SaleItem domain) {
        if (domain == null) {
            return null;
        }
        return SaleItemEntity.builder()
                .id(domain.getId())
                .saleId(domain.getSaleId())
                .productId(domain.getProductId())
                .quantity(domain.getQuantity())
                .unitPrice(domain.getUnitPrice())
                .subtotal(domain.getSubtotal())
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

    public SaleItem toDomain(SaleItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return SaleItem.builder()
                .id(entity.getId())
                .saleId(entity.getSaleId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
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

    public List<SaleItem> toDomainList(List<SaleItemEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
