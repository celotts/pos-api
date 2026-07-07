package com.posapi.application.port.supplier;

import com.posapi.domain.model.supplier.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierManagementPort {
    Supplier createSupplier(Supplier supplier);
    Optional<Supplier> getSupplierById(UUID id);
    List<Supplier> getAllSuppliers();
    Optional<Supplier> updateSupplier(UUID id, Supplier supplier);
    void deleteSupplier(UUID id);
}
