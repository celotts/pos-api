package com.posapi.infrastructure.adapter.output.persistence.repository.sale;

import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {
}
