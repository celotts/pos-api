package com.posapi.application.port.tax;

import com.posapi.domain.model.tax.Tax;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxManagementPort {
    Tax createTax(Tax tax);
    Optional<TaxResponse> getTaxById(UUID id);
    List<TaxResponse> getAllTaxes();
    Optional<TaxResponse> updateTax(UUID id, Tax tax);

    @Transactional(readOnly = true)
    Optional<TaxResponse> getTaxResponseById(UUID id);

    void deleteTax(UUID id);
}
