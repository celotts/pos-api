package com.posapi.infrastructure.adapter.output.persistence.repository;

import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InternalProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsBySku(String sku);
    Optional<ProductEntity> findBySku(String sku);
}
