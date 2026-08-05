package com.posapi.infrastructure.adapter.output.persistence.mapper.purchase;

import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PurchaseItemPersistenceMapper {

    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "createdAt", source = "domain.createdAt")
    @Mapping(target = "updatedAt", source = "domain.updatedAt")
    @Mapping(target = "deletedAt", source = "domain.deletedAt")
    @Mapping(target = "purchase", source = "purchaseEntity")
    @Mapping(target = "product", source = "productEntity")
    @Mapping(target = "createdByUser", expression = "java(domain != null && domain.getCreatedByUserId() != null ? UserEntity.builder().id(domain.getCreatedByUserId()).build() : null)")
    @Mapping(target = "updatedByUser", expression = "java(domain != null && domain.getUpdatedByUserId() != null ? UserEntity.builder().id(domain.getUpdatedByUserId()).build() : null)")
    @Mapping(target = "deletedByUser", expression = "java(domain != null && domain.getDeletedByUserId() != null ? UserEntity.builder().id(domain.getDeletedByUserId()).build() : null)")
    @Mapping(target = "createdByRole", expression = "java(domain != null && domain.getCreatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getCreatedByUserRoleId()).build() : null)")
    @Mapping(target = "updatedByRole", expression = "java(domain != null && domain.getUpdatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getUpdatedByUserRoleId()).build() : null)")
    @Mapping(target = "deletedByRole", expression = "java(domain != null && domain.getDeletedByUserRoleId() != null ? RoleEntity.builder().id(domain.getDeletedByUserRoleId()).build() : null)")
    PurchaseItemEntity toEntity(PurchaseItem domain, PurchaseEntity purchaseEntity, ProductEntity productEntity);

    @Mapping(target = "purchaseId", source = "entity.purchase.id")
    @Mapping(target = "productId", source = "entity.product.id")
    @Mapping(target = "createdByUserId", source = "entity.createdByUser.id")
    @Mapping(target = "updatedByUserId", source = "entity.updatedByUser.id")
    @Mapping(target = "deletedByUserId", source = "entity.deletedByUser.id")
    @Mapping(target = "createdByUserRoleId", source = "entity.createdByRole.id")
    @Mapping(target = "updatedByUserRoleId", source = "entity.updatedByRole.id")
    @Mapping(target = "deletedByUserRoleId", source = "entity.deletedByRole.id")
    PurchaseItem toDomain(PurchaseItemEntity entity);

    List<PurchaseItem> toDomainList(List<PurchaseItemEntity> entities);

    default List<PurchaseItemEntity> toEntityList(List<PurchaseItem> domains, PurchaseEntity purchaseEntity) {
        if (domains == null) {
            return Collections.emptyList();
        }
        return domains.stream()
                .map(domain -> {
                    if (domain == null) {
                        return null;
                    }
                    ProductEntity productEntity = ProductEntity.builder().id(domain.getProductId()).build();
                    return toEntity(domain, purchaseEntity, productEntity);
                })
                .collect(Collectors.toList());
    }
}
