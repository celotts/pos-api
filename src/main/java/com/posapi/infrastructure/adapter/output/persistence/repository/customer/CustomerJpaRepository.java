package com.posapi.infrastructure.adapter.output.persistence.repository.customer;

import com.posapi.infrastructure.adapter.output.persistence.entity.customer.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByRfc(String rfc);
    boolean existsByRfc(String rfc);
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
