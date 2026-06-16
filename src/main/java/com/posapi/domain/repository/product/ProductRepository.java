package com.posapi.domain.repository.product;

import com.posapi.domain.model.product.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    void deleteById(UUID id);
    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.category = :category")
    List<Product> findByProductNameAndCategory(@Param("name") String name, @Param("category") String category);

    // Otros métodos de búsqueda o manipulación de productos que el dominio necesite
}
