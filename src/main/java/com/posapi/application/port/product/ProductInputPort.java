package com.posapi.application.port.product;

import com.posapi.domain.model.product.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductInputPort {

    Product createProduct(Product product);
    Optional<Product> getProductById(UUID id);
    List<Product> getAllProducts();
    Product updateProduct(UUID id, Product updatedProduct);
    void deleteProduct(UUID id);
    Optional<Product> getProductBySku(String sku);
}
