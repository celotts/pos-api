package com.posapi.infrastructure.adapter.output.persistence.repository.purchase;

import com.posapi.infrastructure.adapter.output.persistence.entity.purchase.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchaseJpaRepository extends JpaRepository<PurchaseEntity, UUID> {
    // Spring Data JPA ya proporciona los métodos básicos (save, findById, findAll, etc.)
    // Añade aquí métodos de consulta personalizados si son necesarios (ej. findBySupplierId)
}
