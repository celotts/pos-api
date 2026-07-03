package com.posapi.infrastructure.adapter.input.rest.category;

import com.posapi.application.port.category.CategoryManagementPort;
import com.posapi.domain.model.category.Category;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryManagementPort categoryManagementPort;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = Category.builder().name(request.name()).build();
        Category createdCategory = categoryManagementPort.createCategory(category);
        return new ResponseEntity<>(CategoryResponse.fromDomain(createdCategory), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        return categoryManagementPort.getCategoryById(id)
                .map(CategoryResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryManagementPort.getAllCategories().stream()
                .map(CategoryResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        Category category = Category.builder().name(request.name()).build();
        return categoryManagementPort.updateCategory(id, category)
                .map(CategoryResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryManagementPort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
