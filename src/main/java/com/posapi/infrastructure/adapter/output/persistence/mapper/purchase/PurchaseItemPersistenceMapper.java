package com.posapi.infrastructure.adapter.output.persistence.mapper.purchase;

import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseItemEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PurchaseItemPersistenceMapper {

    public PurchaseItemEntity toEntity(PurchaseItem domain, PurchaseEntity purchaseEntity, ProductEntity productEntity) {
        if (domain == null) {
            return null;
        }
        return PurchaseItemEntity.builder()
                .id(domain.getId())
                .purchase(purchaseEntity)
                .product(productEntity)
                .quantity(domain.getQuantity())
                .unitPrice(domain.getUnitPrice())
                .subtotal(domain.getSubtotal())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                // Asumiendo que UserEntity y RoleEntity se manejan por ID o se resuelven en el servicio
                // Aquí solo mapeamos los IDs si no se cargan las entidades completas
                // Para simplificar, si los IDs son nulos, las relaciones ManyToOne serán nulas
                .createdByUser(Optional.ofNullable(domain.getCreatedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .updatedByUser(Optional.ofNullable(domain.getUpdatedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .deletedByUser(Optional.ofNullable(domain.getDeletedByUserId())
                        .map(id -> UserEntity.builder().id(id).build()).orElse(null))
                .createdByRole(Optional.ofNullable(domain.getCreatedByRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .updatedByRole(Optional.ofNullable(domain.getUpdatedByRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .deletedByRole(Optional.ofNullable(domain.getDeletedByRoleId())
                        .map(id -> RoleEntity.builder().id(id).build()).orElse(null))
                .build();
    }

    public PurchaseItem toDomain(PurchaseItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return PurchaseItem.builder()
                .id(entity.getId())
                .purchaseId(entity.getPurchase() != null ? entity.getPurchase().getId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUser() != null ? entity.getCreatedByUser().getId() : null)
                .updatedByUserId(entity.getUpdatedByUser() != null ? entity.getUpdatedByUser().getId() : null)
                .deletedByUserId(entity.getDeletedByUser() != null ? entity.getDeletedByUser().getId() : null)
                .createdByRoleId(entity.getCreatedByRole() != null ? entity.getCreatedByRole().getId() : null)
                .updatedByRoleId(entity.getUpdatedByRole() != null ? entity.getUpdatedByRole().getId() : null)
                .deletedByRoleId(entity.getDeletedByRole() != null ? entity.getDeletedByRole().getId() : null)
                .build();
    }

    public List<PurchaseItem> toDomainList(List<PurchaseItemEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // Método para mapear una lista de dominios a entidades.
    // Requiere las entidades PurchaseEntity y ProductEntity ya cargadas o referenciadas.
    public List<PurchaseItemEntity> toEntityList(List<PurchaseItem> domains, PurchaseEntity purchaseEntity) {
        return domains.stream()
                .map(domain -> {
                    // Aquí necesitaríamos cargar ProductEntity por su ID si no viene ya en el dominio
                    // Para simplificar, asumimos que el ProductEntity se resolverá en el servicio o adaptador
                    // o que el dominio ya tiene una referencia a ProductEntity.
                    // Por ahora, creamos una referencia a ProductEntity solo con el ID.
                    ProductEntity productEntity = Optional.ofNullable(domain.getProductId())
                            .map(id -> ProductEntity.builder().id(id).build())
                            .orElse(null);
                    return toEntity(domain, purchaseEntity, productEntity);
                })
                .collect(Collectors.toList());
    }
}
