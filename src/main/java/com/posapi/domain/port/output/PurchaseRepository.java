package com.posapi.domain.port.output;

import com.posapi.domain.model.purchase.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository {
    Purchase save(Purchase purchase);
    Optional<Purchase> findById(UUID id);
    List<Purchase> findAll();
    Page<Purchase> findAll(Pageable pageable);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    // Puedes añadir más métodos si el dominio los necesita, por ejemplo:
    // List<Purchase> findBySupplierId(UUID supplierId);
    // Page<Purchase> findByStatus(PurchaseStatus status, Pageable pageable);
}
