package com.posapi.infrastructure.adapter.input.rest.category;

import com.posapi.application.port.category.CategoryManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse; // Importación añadida
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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

        CategoryResponse createdCategory = categoryManagementPort.createCategory(request, currentUser.getId());

        Map<UUID, String> userNames = fetchUserNames(Set.of(createdCategory.createdByUserId())); // Acceso directo al campo
        String createdByName = userNames.get(createdCategory.createdByUserId()); // Acceso directo al campo

        return new ResponseEntity<>(
                CategoryResponse.fromResponse( // Usar fromResponse
                        createdCategory, createdByName, null, null // updatedByName y deletedByName son null para creación
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id, @Valid @RequestBody CategoryRequest request
    ) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();

        return categoryManagementPort.updateCategory(id, request, currentUser.getId())
                .map(updatedCategoryResponse -> {
                    Set<UUID> userIds = Stream.of(
                            updatedCategoryResponse.createdByUserId(), // Acceso directo al campo
                            updatedCategoryResponse.updatedByUserId(), // Acceso directo al campo
                            updatedCategoryResponse.deletedByUserId()  // Acceso directo al campo
                    ).filter(Objects::nonNull).collect(Collectors.toSet());
                    Map<UUID, String> userNames = fetchUserNames(userIds);

                    String createdByName = userNames.get(updatedCategoryResponse.createdByUserId()); // Acceso directo al campo
                    String updatedByName = userNames.get(updatedCategoryResponse.updatedByUserId()); // Acceso directo al campo
                    String deletedByName = userNames.get(updatedCategoryResponse.deletedByUserId()); // Acceso directo al campo

                    return ResponseEntity.ok(CategoryResponse.fromResponse(updatedCategoryResponse, createdByName, updatedByName, deletedByName)); // Usar fromResponse
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // Retorna ResponseEntity<Void> que es compatible con cualquier ResponseEntity<?>
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        return categoryManagementPort.getCategoryById(id)
                .map(categoryResponse -> {
                    Set<UUID> userIds = Stream.of(
                            categoryResponse.createdByUserId(), // Acceso directo al campo
                            categoryResponse.updatedByUserId(), // Acceso directo al campo
                            categoryResponse.deletedByUserId()  // Acceso directo al campo
                    ).filter(Objects::nonNull).collect(Collectors.toSet());
                    Map<UUID, String> userNames = fetchUserNames(userIds);

                    String createdByName = userNames.get(categoryResponse.createdByUserId()); // Acceso directo al campo
                    String updatedByName = userNames.get(categoryResponse.updatedByUserId()); // Acceso directo al campo
                    String deletedByName = userNames.get(categoryResponse.deletedByUserId()); // Acceso directo al campo

                    return ResponseEntity.ok(CategoryResponse.fromResponse(categoryResponse, createdByName, updatedByName, deletedByName)); // Usar fromResponse
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // Retorna ResponseEntity<Void> que es compatible con cualquier ResponseEntity<?>
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> getAllCategories(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        PageResponse<CategoryResponse> pageResponse = categoryManagementPort.getAllCategories(pageable);

        // CORREGIDO: Retornamos el objeto de paginación completo y usamos getContent()
        return ResponseEntity.ok(pageResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        categoryManagementPort.deleteCategory(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
