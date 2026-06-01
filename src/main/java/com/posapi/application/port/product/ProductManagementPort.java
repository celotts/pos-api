package com.posapi.application.port.product;

import com.posapi.domain.model.product.Product;
import jakarta.validation.constraints.NotNull; // Importar NotNull

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductManagementPort {

    @NotNull // Indica que este método siempre devuelve un Product no nulo
    Product createProduct(@NotNull Product product); // También marcamos el parámetro como NotNull
    Optional<Product> getProductById(UUID id);
    @NotNull // Indica que este método siempre devuelve una lista no nula de Product
    List<Product> getAllProducts();
    @NotNull // Indica que este método siempre devuelve un Product no nulo
    Product updateProduct(UUID id, @NotNull Product updatedProduct); // También marcamos el parámetro como NotNull
    void deleteProduct(UUID id);
    Optional<Product> getProductBySku(String sku);
}
