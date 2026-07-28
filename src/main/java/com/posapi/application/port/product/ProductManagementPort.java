package com.posapi.application.port.product;

import com.posapi.domain.model.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductManagementPort {

    Product createProduct(Product product, UUID currentUserId);

    Optional<Product> getProductById(UUID id);

    List<Product> getAllProducts();

    Optional<Product> updateProduct(UUID id, Product product, UUID currentUserId);

    void deleteProduct(UUID id, UUID currentUserId);

    Optional<Product> getProductBySku(String sku);
}
