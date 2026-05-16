package com.posapi.infrastructure.adapter.input.rest.product;

import com.posapi.application.service.product.ProductService; // Actualizado
import com.posapi.domain.model.product.Product; // Actualizado
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest; // Actualizado
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse; // Actualizado
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product product = toDomain(productRequest);
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(toResponse(createdProduct), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return productService.getProductById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest productRequest) {
        Product product = toDomain(productRequest);
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(toResponse(updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Manejo de error básico
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mappers ---
    private Product toDomain(ProductRequest request) {
        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .purchasePrice(request.getPurchasePrice())
                .salePrice(request.getSalePrice())
                .currentStock(request.getCurrentStock())
                .taxId(request.getTaxId())
                .supplierId(request.getSupplierId())
                // ID, created_at, updated_at, etc. se manejan en el servicio o la persistencia
                .build();
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .currentStock(product.getCurrentStock())
                .taxId(product.getTaxId())
                .supplierId(product.getSupplierId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .createdByUserId(product.getCreatedByUserId())
                .updatedByUserId(product.getUpdatedByUserId())
                .deletedByUserId(product.getDeletedByUserId())
                .build();
    }
}
