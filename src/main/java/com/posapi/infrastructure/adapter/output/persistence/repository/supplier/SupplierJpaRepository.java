package com.posapi.infrastructure.adapter.output.persistence.repository.supplier;

import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplierJpaRepository extends JpaRepository<SupplierEntity, UUID> {
    boolean existsByRfc(String rfc);
}
