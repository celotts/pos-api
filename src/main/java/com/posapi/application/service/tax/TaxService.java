package com.posapi.application.service.tax;

import com.posapi.application.port.tax.TaxManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.port.output.TaxRepository;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
import com.posapi.infrastructure.adapter.input.rest.tax.mapper.TaxRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaxService implements TaxManagementPort {

    private final TaxRepository taxRepository;
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository; // Needed for user name enrichment
    private final TaxRestMapper taxRestMapper; // Needed for mapping to TaxResponse

    @Override
    @Transactional
    public Tax createTax(Tax tax) {
        if (taxRepository.existsByName(tax.getName())) {
            throw new DuplicateResourceException("Tax with name '" + tax.getName() + "' already exists.");
        }
        User currentUser = securityContextHelper.getCurrentUserOrThrow(); // 🛡️ World-Class: Get current user once
        tax.setId(UUID.randomUUID());
        tax.setCreatedBy(currentUser.getId());
        // createdAt and updatedAt are handled by JPA Auditing
        return taxRepository.save(tax);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaxResponse> getTaxById(UUID id) {
        return taxRepository.findById(id)
                .map(this::mapTaxToTaxResponse); // 🛡️ World-Class: Centralize enrichment logic
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxResponse> getAllTaxes() {
        List<Tax> taxes = taxRepository.findAll(); // Get domain models
        return taxes.stream()
                .map(this::mapTaxToTaxResponse) // 🛡️ World-Class: Centralize enrichment logic
                .collect(Collectors.toList()).reversed();
    }

    @Override
    @Transactional
    public Optional<TaxResponse> updateTax(UUID id, Tax tax) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        return taxRepository.findById(id).map(existingTax -> {
            existingTax.setName(tax.getName());
            existingTax.setPercentage(tax.getPercentage());
            existingTax.setTaxType(tax.getTaxType());
            existingTax.setUpdatedBy(currentUser.getId());
            Tax updatedTax = taxRepository.save(existingTax);
            return mapTaxToTaxResponse(updatedTax); // Return enriched DTO
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TaxResponse> getTaxResponseById(UUID id) {
        return taxRepository.findById(id)
                .map(this::mapTaxToTaxResponse);
    }

    @Override
    @Transactional
    public void deleteTax(UUID id) {
        taxRepository.deleteById(id);
    }

    // 🛡️ World-Class: Private helper method to enrich Tax domain model with user names
    private TaxResponse mapTaxToTaxResponse(Tax tax) {
        Set<UUID> userIds = Stream.of(tax.getCreatedBy(), tax.getUpdatedBy())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(tax.getCreatedBy(), null);
        String updatedByName = userNames.getOrDefault(tax.getUpdatedBy(), null);

        return TaxResponse.fromDomain(tax, createdByName, updatedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
