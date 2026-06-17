package com.posapi.infrastructure.adapter.output.persistence.product;

import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findBySku(String sku);
}
