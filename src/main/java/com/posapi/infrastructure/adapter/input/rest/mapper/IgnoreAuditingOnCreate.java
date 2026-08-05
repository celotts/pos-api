package com.posapi.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "createdAt", ignore = true)
@Mapping(target = "updatedAt", ignore = true)
@Mapping(target = "deletedAt", ignore = true)
@Mapping(target = "createdByUserId", ignore = true)
@Mapping(target = "updatedByUserId", ignore = true)
@Mapping(target = "deletedByUserId", ignore = true)
@Mapping(target = "createdByUserRoleId", ignore = true)
@Mapping(target = "updatedByUserRoleId", ignore = true)
@Mapping(target = "deletedByUserRoleId", ignore = true)
public @interface IgnoreAuditingOnCreate {
}
