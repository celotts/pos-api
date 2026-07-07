package com.posapi.infrastructure.adapter.input.rest.category;

import com.posapi.application.port.category.CategoryManagementPort;
import com.posapi.domain.model.category.Category;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryManagementPort categoryManagementPort;
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();

        Category categoryToCreate = Category.builder()
                .name(request.name())
                .createdBy(currentUser.getId())
                .build();

        Category createdCategory = categoryManagementPort.createCategory(categoryToCreate);

        return new ResponseEntity<>(
                CategoryResponse.fromDomain(createdCategory, currentUser.getFullName(), null),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id, @Valid @RequestBody CategoryRequest request
    ) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();

        Category categoryToUpdate = Category.builder()
                .name(request.name())
                .updatedBy(currentUser.getId())
                .build();

        return categoryManagementPort.updateCategory(id, categoryToUpdate)
                .map(updatedCategory -> {
                    Set<UUID> userIds = Stream.of(updatedCategory.getCreatedBy(), updatedCategory.getUpdatedBy())
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    return toResponse(updatedCategory, fetchUserNames(userIds));
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        return categoryManagementPort.getCategoryById(id)
                .map(category -> {
                    Set<UUID> userIds = Stream.of(category.getCreatedBy(), category.getUpdatedBy())
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    return toResponse(category, fetchUserNames(userIds));
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryManagementPort.getAllCategories();
        Set<UUID> userIds = categories.stream()
                .flatMap(cat -> Stream.of(cat.getCreatedBy(), cat.getUpdatedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        List<CategoryResponse> responses = categories.stream()
                .map(category -> toResponse(category, userNames))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryManagementPort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    private CategoryResponse toResponse(Category category, Map<UUID, String> userNames) {
        String createdByName = category.getCreatedBy() != null ? userNames.get(category.getCreatedBy()) : null;
        String updatedByName = category.getUpdatedBy() != null ? userNames.get(category.getUpdatedBy()) : null;
        return CategoryResponse.fromDomain(category, createdByName, updatedByName);
    }
}
