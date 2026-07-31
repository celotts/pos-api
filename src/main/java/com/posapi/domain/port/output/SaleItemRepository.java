package com.posapi.domain.port.output;

import com.posapi.domain.model.sale.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleItemRepository {
    SaleItem save(SaleItem saleItem);
    Optional<SaleItem> findById(UUID id);
    List<SaleItem> findAllBySaleId(UUID saleId); // Método personalizado para obtener ítems de una venta
    Page<SaleItem> findAll(Pageable pageable);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    void delete(SaleItem entity);
    void deleteAllById(Iterable<? extends UUID> ids);
}
