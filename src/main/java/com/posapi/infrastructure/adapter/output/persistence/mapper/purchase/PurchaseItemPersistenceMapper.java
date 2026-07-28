package com.posapi.infrastructure.adapter.output.persistence.mapper.purchase;

import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
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
    @Mapping(target = "createdByUser", ignore = true) // El dominio PurchaseItem ya no tiene createdByRole
    @Mapping(target = "updatedByUser", ignore = true) // El dominio PurchaseItem ya no tiene updatedByRole
    @Mapping(target = "deletedByUser", ignore = true) // El dominio PurchaseItem ya no tiene deletedByRole
    @Mapping(target = "createdByRole", ignore = true) // El dominio PurchaseItem ya no tiene createdByRole
    @Mapping(target = "updatedByRole", ignore = true) // El dominio PurchaseItem ya no tiene updatedByRole
    @Mapping(target = "deletedByRole", ignore = true) // El dominio PurchaseItem ya no tiene deletedByRole
    PurchaseItemEntity toEntity(PurchaseItem domain, PurchaseEntity purchaseEntity, ProductEntity productEntity);

    // Mapeo de entidad de persistencia a dominio
    @Mapping(target = "purchaseId", source = "entity.purchase.id")
    @Mapping(target = "productId", source = "entity.product.id")
    @Mapping(target = "createdByUserId", source = "entity.createdByUser.id")
    @Mapping(target = "updatedByUserId", source = "entity.updatedByUser.id")
    @Mapping(target = "deletedByUserId", source = "entity.deletedByUser.id")
    PurchaseItem toDomain(PurchaseItemEntity entity);

    List<PurchaseItem> toDomainList(List<PurchaseItemEntity> entities);

    // Método para mapear una lista de dominios a entidades.
    // Requiere las entidades PurchaseEntity y ProductEntity ya cargadas o referenciadas.
    default List<PurchaseItemEntity> toEntityList(List<PurchaseItem> domains, PurchaseEntity purchaseEntity) {
        return domains.stream()
                .map(domain -> {
                    // Aquí necesitaríamos cargar ProductEntity por su ID si no viene ya en el dominio
                    // Para simplificar, asumimos que el ProductEntity se resolverá en el servicio o adaptador
                    // o que el dominio ya tiene una referencia a ProductEntity.
                    // Por ahora, creamos una referencia a ProductEntity solo con el ID.
                    ProductEntity productEntity = ProductEntity.builder().id(domain.getProductId()).build();
                    return toEntity(domain, purchaseEntity, productEntity);
                })
                .collect(Collectors.toList());
    }
}
