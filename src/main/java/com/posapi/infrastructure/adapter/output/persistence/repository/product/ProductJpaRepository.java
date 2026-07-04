package com.posapi.infrastructure.adapter.output.persistence.repository.product;

import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySku(String sku);

    @Query("SELECT p FROM ProductEntity p JOIN p.category c "
           + "WHERE p.name LIKE %:name% AND c.name LIKE %:categoryName%")
    List<ProductEntity> findByProductNameAndCategoryName(
            @Param("name") String name, @Param("categoryName") String categoryName);
    
    // Asumiendo que tienes un método para borrado lógico
    default void softDeleteById(UUID id) {
        findById(id).ifPresent(product -> {
            product.setDeletedAt(java.time.Instant.now());
            save(product);
        });
    }
}
