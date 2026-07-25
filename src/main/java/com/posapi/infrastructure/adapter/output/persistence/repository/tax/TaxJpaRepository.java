package com.posapi.infrastructure.adapter.output.persistence.repository.tax;

import com.posapi.infrastructure.adapter.output.persistence.entity.tax.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // AÑADIDO
import java.util.UUID;

@Repository
public interface TaxJpaRepository extends JpaRepository<TaxEntity, UUID> {
    boolean existsByName(String name);
    Optional<TaxEntity> findByName(String name); // AÑADIDO
}
