package com.posapi.domain.port.output;

import com.posapi.domain.model.tax.Tax;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxRepositoryPort {
    Tax save(Tax tax);
    Optional<Tax> findById(UUID id);
    List<Tax> findAll();
    boolean delete(UUID id);
}
