package com.posapi.infrastructure.adapter.output.persistence.repository.product;

import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySku(String sku);

    @Query("SELECT p FROM ProductEntity p WHERE p.name = :name AND p.category.name = :categoryName")
    List<ProductEntity> findByProductNameAndCategoryName(@Param("name") String name, @Param("categoryName") String categoryName);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :id")
    void softDeleteById(@Param("id") UUID id);
}
