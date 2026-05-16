package com.posapi.domain.repository.product;

import com.posapi.domain.model.product.Product; // Actualizado
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    void deleteById(UUID id);
    Optional<Product> findBySku(String sku);
    // Otros métodos de búsqueda o manipulación de productos que el dominio necesite
}
