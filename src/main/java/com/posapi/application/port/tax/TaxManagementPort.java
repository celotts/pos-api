package com.posapi.application.port.tax;

import com.posapi.domain.model.tax.Tax;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxManagementPort {
    Tax createTax(Tax tax);
    Optional<Tax> getTaxById(UUID id);
    List<Tax> getAllTaxes();
    Optional<Tax> updateTax(UUID id, Tax tax);
    void deleteTax(UUID id);
}
