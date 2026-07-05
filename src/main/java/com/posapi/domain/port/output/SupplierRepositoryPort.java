package com.posapi.domain.port.output;

import com.posapi.domain.model.supplier.Supplier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepositoryPort {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(UUID id);
    List<Supplier> findAll();
    boolean delete(UUID id); // O lógica de borrado lógico
    boolean existsByRfc(String rfc);
}
