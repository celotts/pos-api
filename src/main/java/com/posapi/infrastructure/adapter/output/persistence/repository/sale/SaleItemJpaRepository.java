package com.posapi.infrastructure.adapter.output.persistence.repository.sale;

import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleItemJpaRepository extends JpaRepository<SaleItemEntity, UUID> {
    List<SaleItemEntity> findAllBySaleId(UUID saleId);
}
