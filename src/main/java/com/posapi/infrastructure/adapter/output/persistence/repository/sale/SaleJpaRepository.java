package com.posapi.infrastructure.adapter.output.persistence.repository.sale;

import com.posapi.domain.model.sale.SaleStatus; // Importar SaleStatus
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant; // Importar Instant
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {
    // Puedes añadir métodos de consulta personalizados aquí si son necesarios para el adaptador

    /**
     * Busca todas las ventas de un cliente específico.
     * @param customerId ID del cliente.
     * @return Lista de entidades de venta.
     */
    List<SaleEntity> findByCustomerId(UUID customerId);

    /**
     * Busca ventas dentro de un rango de fechas.
     * @param start Fecha de inicio del rango (inclusive).
     * @param end Fecha de fin del rango (inclusive).
     * @return Lista de entidades de venta.
     */
    List<SaleEntity> findBySaleDateBetween(Instant start, Instant end);

    /**
     * Busca ventas con un estado particular.
     * @param status Estado de la venta.
     * @return Lista de entidades de venta.
     */
    List<SaleEntity> findByStatus(SaleStatus status);
}
