package com.posapi.domain.port.output;

import com.posapi.domain.model.product.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    void deleteById(UUID id);
    boolean existsBySku(String sku);
    Optional<Product> findBySku(String sku);
}
