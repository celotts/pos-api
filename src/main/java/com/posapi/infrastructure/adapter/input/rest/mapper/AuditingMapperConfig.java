package com.posapi.infrastructure.adapter.input.rest.mapper;

import com.posapi.domain.model.base.BaseModel;
import com.posapi.infrastructure.adapter.input.rest.dto.BaseResponse;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditingMapperConfig {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdByUserId", target = "createdByUserId")
    @Mapping(source = "updatedByUserId", target = "updatedByUserId")
    @Mapping(source = "createdByUserRoleId", target = "createdByUserRoleId")
    @Mapping(source = "updatedByUserRoleId", target = "updatedByUserRoleId")
    void toBaseResponse(BaseModel source, @MappingTarget BaseResponse target);
}
