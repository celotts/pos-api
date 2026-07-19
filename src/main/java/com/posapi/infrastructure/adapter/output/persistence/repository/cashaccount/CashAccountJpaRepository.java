package com.posapi.infrastructure.adapter.output.persistence.repository.cashaccount;

import com.posapi.infrastructure.adapter.output.persistence.entity.cashaccount.CashAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashAccountJpaRepository extends JpaRepository<CashAccountEntity, UUID> {
    Optional<CashAccountEntity> findByName(String name);
    boolean existsByName(String name);
}
