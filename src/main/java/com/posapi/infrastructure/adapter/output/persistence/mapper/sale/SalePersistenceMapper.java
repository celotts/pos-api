package com.posapi.infrastructure.adapter.output.persistence.mapper.sale;

import com.posapi.domain.model.sale.Sale;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalePersistenceMapper {

    @Mapping(target = "customer", expression = "java(domain.getCustomerId() != null ? CustomerEntity.builder().id(domain.getCustomerId()).build() : null)")
    @Mapping(target = "posTerminal", expression = "java(domain.getPosTerminalId() != null ? PosTerminalEntity.builder().id(domain.getPosTerminalId()).build() : null)")
    @Mapping(target = "shift", expression = "java(domain.getShiftId() != null ? ShiftEntity.builder().id(domain.getShiftId()).build() : null)")
    @Mapping(target = "createdByUser", expression = "java(domain.getCreatedByUserId() != null ? UserEntity.builder().id(domain.getCreatedByUserId()).build() : null)")
    @Mapping(target = "updatedByUser", expression = "java(domain.getUpdatedByUserId() != null ? UserEntity.builder().id(domain.getUpdatedByUserId()).build() : null)")
    @Mapping(target = "deletedByUser", expression = "java(domain.getDeletedByUserId() != null ? UserEntity.builder().id(domain.getDeletedByUserId()).build() : null)")
    @Mapping(target = "createdByRole", expression = "java(domain.getCreatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getCreatedByUserRoleId()).build() : null)")
    @Mapping(target = "updatedByRole", expression = "java(domain.getUpdatedByUserRoleId() != null ? RoleEntity.builder().id(domain.getUpdatedByUserRoleId()).build() : null)")
    @Mapping(target = "deletedByRole", expression = "java(domain.getDeletedByUserRoleId() != null ? RoleEntity.builder().id(domain.getDeletedByUserRoleId()).build() : null)")
    SaleEntity toEntity(Sale domain);

    @Mapping(target = "customerId", source = "entity.customer.id")
    @Mapping(target = "posTerminalId", source = "entity.posTerminal.id")
    @Mapping(target = "shiftId", source = "entity.shift.id")
    @Mapping(target = "createdByUserId", source = "entity.createdByUser.id")
    @Mapping(target = "updatedByUserId", source = "entity.updatedByUser.id")
    @Mapping(target = "deletedByUserId", source = "entity.deletedByUser.id")
    @Mapping(target = "createdByUserRoleId", source = "entity.createdByRole.id")
    @Mapping(target = "updatedByUserRoleId", source = "entity.updatedByRole.id")
    @Mapping(target = "deletedByUserRoleId", source = "entity.deletedByRole.id")
    Sale toDomain(SaleEntity entity);

    List<Sale> toDomainList(List<SaleEntity> entities);
}
