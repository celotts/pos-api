package com.posapi.application.port.product;

import com.posapi.domain.model.product.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductManagementPort {
    Product createProduct(Product product);
    Optional<Product> getProductById(UUID id);
    List<Product> getAllProducts();
    Optional<Product> updateProduct(UUID id, Product product);
    void deleteProduct(UUID id);
    Optional<Product> getProductBySku(String sku); // <-- AÑADIDO
}
