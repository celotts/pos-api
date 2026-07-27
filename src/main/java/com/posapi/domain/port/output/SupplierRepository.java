package com.posapi.domain.port.output;

import com.posapi.domain.model.supplier.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(UUID id);
    List<Supplier> findAll();
    void deleteById(UUID id);
    boolean existsByRfc(String rfc);

    boolean existsById(UUID supplierId);
}
