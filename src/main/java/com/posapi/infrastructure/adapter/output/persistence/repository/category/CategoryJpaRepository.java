package com.posapi.infrastructure.adapter.output.persistence.repository.category;

import com.posapi.infrastructure.adapter.output.persistence.entity.category.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByName(String name);
}
