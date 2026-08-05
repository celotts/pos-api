package com.posapi.infrastructure.adapter.output.persistence.mapper.category;

import com.posapi.domain.model.category.Category;
import com.posapi.infrastructure.adapter.output.persistence.entity.category.CategoryEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    CategoryEntity toEntity(Category domain);

    @InheritInverseConfiguration
    Category toDomain(CategoryEntity entity);

    List<Category> toDomainList(List<CategoryEntity> entities);
}
