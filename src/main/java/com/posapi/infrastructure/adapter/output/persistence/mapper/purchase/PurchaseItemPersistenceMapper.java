package com.posapi.infrastructure.adapter.output.persistence.mapper.purchase;

import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity; // Importar RoleEntity
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity; // Importar UserEntity
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PurchaseItemPersistenceMapper {

    // Mapeo de dominio a entidad de persistencia
    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "createdAt", source = "domain.createdAt")
    @Mapping(target = "updatedAt", source = "domain.updatedAt")
    @Mapping(target = "deletedAt", source = "domain.deletedAt")
    @Mapping(target = "purchase", source = "purchaseEntity")
    @Mapping(target = "product", source = "productEntity")
    // Mapear los IDs de usuario y rol a las entidades relacionadas
    @Mapping(target = "createdByUser", expression = "java(domain.getCreatedByUserId() != null ? UserEntity.builder().id(domain.getCreatedByUserId()).build() : null)")
    @Mapping(target = "updatedByUser", expression = "java(domain.getUpdatedByUserId() != null ? UserEntity.builder().id(domain.getUpdatedByUserId()).build() : null)")
    @Mapping(target = "deletedByUser", expression = "java(domain.getDeletedByUserId() != null ? UserEntity.builder().id(domain.getDeletedByUserId()).build() : null)")
    @Mapping(target = "createdByRole", expression = "java(domain.getCreatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getCreatedByUserRoleId()).build() : null)")
    @Mapping(target = "updatedByRole", expression = "java(domain.getUpdatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getUpdatedByUserRoleId()).build() : null)")
    @Mapping(target = "deletedByRole", expression = "java(domain.getDeletedByUserRoleId() != null ? RoleEntity.builder().id(domain.getDeletedByUserRoleId()).build() : null)")
    PurchaseItemEntity toEntity(PurchaseItem domain, PurchaseEntity purchaseEntity, ProductEntity productEntity);

    // Mapeo de entidad de persistencia a dominio
    @Mapping(target = "purchaseId", source = "entity.purchase.id")
    @Mapping(target = "productId", source = "entity.product.id")
    // Mapear los IDs de usuario y rol desde las entidades relacionadas
    @Mapping(target = "createdByUserId", source = "entity.createdByUser.id")
    @Mapping(target = "updatedByUserId", source = "entity.updatedByUser.id")
    @Mapping(target = "deletedByUserId", source = "entity.deletedByUser.id")
    @Mapping(target = "createdByUserRoleId", source = "entity.createdByRole.id")
    @Mapping(target = "updatedByUserRoleId", source = "entity.updatedByRole.id")
    @Mapping(target = "deletedByUserRoleId", source = "entity.deletedByRole.id")
    PurchaseItem toDomain(PurchaseItemEntity entity);

    List<PurchaseItem> toDomainList(List<PurchaseItemEntity> entities);

    // Método para mapear una lista de dominios a entidades.
    // Requiere las entidades PurchaseEntity y ProductEntity ya cargadas o referenciadas.
    default List<PurchaseItemEntity> toEntityList(List<PurchaseItem> domains, PurchaseEntity purchaseEntity) {
        return domains.stream()
                .map(domain -> {
                    ProductEntity productEntity = ProductEntity.builder().id(domain.getProductId()).build();
                    return toEntity(domain, purchaseEntity, productEntity);
                })
                .collect(Collectors.toList());
    }
}
