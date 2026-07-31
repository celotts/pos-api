package com.posapi.domain.port.output;

import com.posapi.domain.model.sale.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository {
    Sale save(Sale sale);
    Optional<Sale> findById(UUID id);
    List<Sale> findAll();
    Page<Sale> findAll(Pageable pageable);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    long count();
    void delete(Sale entity);
    void deleteAll();
    void deleteAllById(Iterable<? extends UUID> ids);
}
