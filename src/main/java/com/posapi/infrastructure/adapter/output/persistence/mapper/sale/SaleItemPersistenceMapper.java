package com.posapi.infrastructure.adapter.output.persistence.mapper.sale;

import com.posapi.domain.model.sale.SaleItem;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SaleItemPersistenceMapper {

    // Mapeo de dominio a entidad de persistencia
    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "createdAt", source = "domain.createdAt")
    @Mapping(target = "updatedAt", source = "domain.updatedAt")
    @Mapping(target = "deletedAt", source = "domain.deletedAt")
    @Mapping(target = "sale", source = "saleEntity")
    @Mapping(target = "product", source = "productEntity")
    @Mapping(target = "createdByUser", expression = "java(domain.getCreatedByUserId() != null ? UserEntity.builder().id(domain.getCreatedByUserId()).build() : null)")
    @Mapping(target = "updatedByUser", expression = "java(domain.getUpdatedByUserId() != null ? UserEntity.builder().id(domain.getUpdatedByUserId()).build() : null)")
    @Mapping(target = "deletedByUser", expression = "java(domain.getDeletedByUserId() != null ? UserEntity.builder().id(domain.getDeletedByUserId()).build() : null)")
    @Mapping(target = "createdByRole", expression = "java(domain.getCreatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getCreatedByUserRoleId()).build() : null)")
    @Mapping(target = "updatedByRole", expression = "java(domain.getUpdatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getUpdatedByUserRoleId()).build() : null)")
    @Mapping(target = "deletedByRole", expression = "java(domain.getDeletedByUserRoleId() != null ? RoleEntity.builder().id(domain.getDeletedByUserRoleId()).build() : null)")
    SaleItemEntity toEntity(SaleItem domain, SaleEntity saleEntity, ProductEntity productEntity);

    // Mapeo de entidad de persistencia a dominio
    @Mapping(target = "saleId", source = "entity.sale.id")
    @Mapping(target = "productId", source = "entity.product.id")
    @Mapping(target = "createdByUserId", source = "entity.createdByUser.id")
    @Mapping(target = "updatedByUserId", source = "entity.updatedByUser.id")
    @Mapping(target = "deletedByUserId", source = "entity.deletedByUser.id")
    @Mapping(target = "createdByUserRoleId", source = "entity.createdByRole.id")
    @Mapping(target = "updatedByUserRoleId", source = "entity.updatedByRole.id")
    @Mapping(target = "deletedByUserRoleId", source = "entity.deletedByRole.id")
    SaleItem toDomain(SaleItemEntity entity);

    List<SaleItem> toDomainList(List<SaleItemEntity> entities);

    default List<SaleItemEntity> toEntityList(List<SaleItem> domains, SaleEntity saleEntity) {
        return domains.stream()
                .map(domain -> {
                    ProductEntity productEntity = ProductEntity.builder().id(domain.getProductId()).build();
                    return toEntity(domain, saleEntity, productEntity);
                })
                .collect(Collectors.toList());
    }
}
