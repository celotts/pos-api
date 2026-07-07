package com.posapi.application.service.tax;

import com.posapi.application.port.tax.TaxManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.TaxRepository;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaxService implements TaxManagementPort {

    private final TaxRepository taxRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public Tax createTax(Tax tax) {
        if (taxRepository.existsByName(tax.getName())) {
            throw new DuplicateResourceException("Tax with name '" + tax.getName() + "' already exists.");
        }
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        tax.setId(UUID.randomUUID());
        tax.setCreatedBy(currentUser.getId());
        return taxRepository.save(tax);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tax> getTaxById(UUID id) {
        return taxRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Tax> updateTax(UUID id, Tax tax) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        return taxRepository.findById(id).map(existingTax -> {
            existingTax.setName(tax.getName());
            existingTax.setPercentage(tax.getPercentage());
            existingTax.setTaxType(tax.getTaxType());
            existingTax.setUpdatedBy(currentUser.getId());
            return taxRepository.save(existingTax);
        });
    }

    @Override
    @Transactional
    public void deleteTax(UUID id) {
        taxRepository.deleteById(id);
    }
}
