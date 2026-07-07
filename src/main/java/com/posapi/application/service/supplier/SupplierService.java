package com.posapi.application.service.supplier;

import com.posapi.application.port.supplier.SupplierManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.supplier.Supplier;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.SupplierRepository;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierManagementPort {

    private final SupplierRepository supplierRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        if (supplierRepository.existsByRfc(supplier.getRfc())) {
            throw new DuplicateResourceException("Supplier with RFC '" + supplier.getRfc() + "' already exists.");
        }
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        supplier.setId(UUID.randomUUID());
        supplier.setCreatedBy(currentUser.getId());
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierById(UUID id) {
        return supplierRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Supplier> updateSupplier(UUID id, Supplier supplier) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        return supplierRepository.findById(id)
                .map(existingSupplier -> {
                    existingSupplier.setBusinessName(supplier.getBusinessName());
                    existingSupplier.setTaxRegimen(supplier.getTaxRegimen());
                    existingSupplier.setContactEmail(supplier.getContactEmail());
                    existingSupplier.setUpdatedBy(currentUser.getId());
                    return supplierRepository.save(existingSupplier);
                });
    }

    @Override
    @Transactional
    public void deleteSupplier(UUID id) {
        supplierRepository.deleteById(id);
    }
}
