package com.posapi.infrastructure.adapter.input.rest.category.mapper;

import com.posapi.domain.model.category.Category;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;
import com.posapi.infrastructure.adapter.input.rest.mapper.AuditingMapperConfig;
import com.posapi.infrastructure.adapter.input.rest.mapper.IgnoreAuditingOnCreate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class)
public interface CategoryRestMapper {

    @IgnoreAuditingOnCreate
    Category toDomain(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
