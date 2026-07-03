package com.posapi.infrastructure.adapter.input.rest.product;

import com.posapi.application.service.product.ProductService;
import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import com.posapi.infrastructure.adapter.input.rest.product.mapper.ProductRestMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRestMapper productMapper;

    public ProductController(ProductService productService, ProductRestMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product product = productMapper.toDomain(productRequest);
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(productMapper.toResponse(createdProduct), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public HttpEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return productService.getProductById(id)
                .map(productMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id,
            @Valid @RequestBody ProductRequest productRequest) {
        try {
            Product product = productMapper.toDomain(productRequest);
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(productMapper.toResponse(updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @NotNull UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
